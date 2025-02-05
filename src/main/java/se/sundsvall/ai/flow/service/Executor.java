package se.sundsvall.ai.flow.service;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import se.sundsvall.ai.flow.integration.intric.IntricIntegration;
import se.sundsvall.ai.flow.model.flowdefinition.FlowInput;
import se.sundsvall.ai.flow.model.session.Input;
import se.sundsvall.ai.flow.model.session.Session;
import se.sundsvall.ai.flow.model.support.StringMultipartFile;
import se.sundsvall.ai.flow.model.flowdefinition.FlowInputRef;
import se.sundsvall.ai.flow.model.flowdefinition.RedirectedOutput;
import se.sundsvall.ai.flow.model.session.StepExecution;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class Executor {

	private static final Logger LOG = LoggerFactory.getLogger(Executor.class);

	private static final String INTRIC_INPUT_TEMPLATE = "%s%s:\n%s\n";

	private final SessionService sessionService;
	private final IntricIntegration intricIntegration;

	public Executor(final SessionService sessionService, final IntricIntegration intricIntegration) {
		this.sessionService = sessionService;
		this.intricIntegration = intricIntegration;
	}

	@Async
	public void executeSession(final Session session) {
		var flow = session.getFlow();
		var sessionInput = session.getInput();

		// Upload all inputs (files) in the local session that haven't been uploaded before
		uploadMissingInputFilesInSessionToIntric(session);

		// Re-map the flow inputs as a map from input id to actual input, for easier access
		var flowInputs = flow.getFlowInputs().stream()
			.collect(toMap(FlowInput::getId, input -> input));

		// Mark the session as running
		session.setState(Session.State.RUNNING);
		// Execute the steps in the order defined in the flow
		flow.getSteps().stream()
			.map(step -> session.getStepExecution(step.getId()))
			.forEach(this::executeStepInternal);
		// Mark the session as finished
		session.setState(Session.State.FINISHED);

		// TODO: setup a scheduled reaper job that checks all available sessions for any where
		// no step execution has been updated in 1 hour or whatever interval and terminate each one,
		// thus deleting all uploaded files related to it
	}

	@Async
	public void executeStep(final StepExecution stepExecution) {
		var session = stepExecution.getSession();

		// The session must either be running or finished before allowing any individual steps to be executed individually
		if (session.getState() != Session.State.RUNNING && session.getState() != Session.State.FINISHED) {
			throw Problem.valueOf(Status.INTERNAL_SERVER_ERROR, "Unable to run step '%s' in flow '%s' for session %s since the session has never been run yet".formatted(stepExecution.getStep().getId(), session.getFlow().getName(), session.getId()));
		}

		// Make sure the step isn't already running - TODO: should this cause an error or just return ???
		if (stepExecution.isRunning()) {
			throw Problem.valueOf(Status.INTERNAL_SERVER_ERROR, "Unable to run already running step '%s' in flow '%s' for session %s".formatted(stepExecution.getStep().getId(), session.getFlow().getName(), session.getId()));
		}

		executeStepInternal(stepExecution);
	}

	void executeStepInternal(final StepExecution stepExecution) {
		executeStepInternal(stepExecution, null);
	}

	void executeStepInternal(final StepExecution stepExecution, final String question) {
		var session = stepExecution.getSession();
		var flow = session.getFlow();
		var step = stepExecution.getStep();

		LOG.info("Executing step '{}' in flow '{}' for session {}", step.getName(), flow.getName(), session.getId());

		// Recursively execute required steps, if any
		for (var requiredStepExecution : stepExecution.getRequiredStepExecutions()) {
			LOG.info("Triggering step '{}' required by step '{}' in flow '{}' for session {}", requiredStepExecution.getStep().getName(), step.getName(), flow.getName(), session.getId());

			executeStepInternal(requiredStepExecution);
		}

		// Mark the step execution as running
		stepExecution.setState(StepExecution.State.RUNNING);

		// Re-map the flow inputs as a map from input id to actual input, for easier access later on
		var flowInputs = flow.getFlowInputs().stream()
			.collect(toMap(FlowInput::getId, input -> input));

		// Extract inputs that are "regular" flow input references
		var flowInputRefStepInputs = step.getInputs().stream()
			.filter(FlowInputRef.class::isInstance)
			.map(FlowInputRef.class::cast)
			.toList();

		// Extract inputs that are redirected output from other steps
		var redirectedOutputStepInputs = step.getInputs().stream()
			.filter(RedirectedOutput.class::isInstance)
			.map(RedirectedOutput.class::cast)
			.toList();

		// Add any redirected output inputs to the session
		redirectedOutputStepInputs.forEach(redirectedOutputInput -> {
				// Get the dependent step execution
				var dependentStepExecution = session.getStepExecutions().get(redirectedOutputInput.getStep());
				// Wrap the output of the dependent step execution in a StringMultipartFile
				var dependentStepOutputMultipartFile = new StringMultipartFile(session.getFlow().getInputPrefix(), redirectedOutputInput.getName(), dependentStepExecution.getOutput());
				// Add it as an input to the session
				session.addInput(redirectedOutputInput.getStep(), dependentStepOutputMultipartFile);
			});

		// At this point we may have inputs that haven't been uploaded to Intric yet - upload them
		uploadMissingInputFilesInSessionToIntric(session);

		// Join both input types to get all inputs actually in use for the current step execution
		var inputsInUse = Stream.concat(
			flowInputRefStepInputs.stream().map(FlowInputRef::getInput),
			redirectedOutputStepInputs.stream().map(RedirectedOutput::getStep)).toList();

		// Extract the input files (ie Intric file id:s) for the inputs actually in use for the current step execution
		var inputFilesInUse = session.getInput().entrySet().stream()
			.filter(entry -> inputsInUse.contains(entry.getKey()))
			.flatMap(entry -> entry.getValue().stream())
			.map(Input::getIntricFileId)
			.toList();

		// Create an additional instruction on what information lies within each input
		var inputsInUseInfo = session.getInputInfo().entrySet().stream()
			.filter(entry -> inputsInUse.contains(entry.getKey()))
			.map(Map.Entry::getValue)
			.collect(joining());

		// Get the Intric endpoint id
		var intricEndpointId = step.getIntricEndpoint().id();

		try {
			switch (step.getIntricEndpoint().type()) {
				case SERVICE -> {
					// Run the service
					var response = intricIntegration.runService(intricEndpointId, inputFilesInUse, inputsInUseInfo);
					// Store the answer in the step execution
					stepExecution.setOutput(response.answer());
				}
				case ASSISTANT -> {
					// Are we asking a question or a follow-up?
					if (stepExecution.getIntricSessionId() == null) {
						// "Ask" the assistant
						var response = intricIntegration.askAssistant(intricEndpointId, inputFilesInUse, inputsInUseInfo);
						// Store the Intric session id in the step execution to be able to ask follow-ups
						stepExecution.setIntricSessionId(response.sessionId());
						// Store the (current) answer in the step execution
						stepExecution.setOutput(response.answer());
					} else {
						// "Ask" the assistant a follow-up
						var response = intricIntegration.askAssistantFollowup(intricEndpointId, stepExecution.getIntricSessionId(), question);
						// Store the (current) answer in the step execution
						stepExecution.setOutput(response.answer());
					}
				}
			}
			stepExecution.setState(StepExecution.State.DONE);
		} catch (Exception e) {
			stepExecution.setState(StepExecution.State.ERROR);
			stepExecution.setErrorMessage(e.getMessage());
		}
	}

	void uploadMissingInputFilesInSessionToIntric(final Session session) {
		session.getInput().values().stream()
			.flatMap(Collection::stream)
			.filter(not(Input::isUploadedToIntric))
			.forEach(inputFile -> {
				// Upload the file to Intric
				var intricFileId = intricIntegration.uploadFile(inputFile.getFile());
				// Keep a reference to it for later
				inputFile.setIntricFileId(intricFileId);
			});
	}
}

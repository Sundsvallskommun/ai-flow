package se.sundsvall.ai.flow.service;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.zalando.problem.Status.NOT_FOUND;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import se.sundsvall.ai.flow.integration.intric.IntricIntegration;
import se.sundsvall.ai.flow.integration.templating.TemplatingIntegration;
import se.sundsvall.ai.flow.model.flowdefinition.Flow;
import se.sundsvall.ai.flow.model.session.Input;
import se.sundsvall.ai.flow.model.session.Session;
import se.sundsvall.ai.flow.model.flowdefinition.FlowInputRef;
import se.sundsvall.ai.flow.model.flowdefinition.RedirectedOutput;
import se.sundsvall.ai.flow.model.flowdefinition.Step;
import se.sundsvall.ai.flow.model.session.StepExecution;

@Service
public class SessionService {

	private static final Logger LOG = LoggerFactory.getLogger(SessionService.class);

	private final Map<UUID, Session> sessions = new ConcurrentHashMap<>();

	private final IntricIntegration intricIntegration;
	private final TemplatingIntegration templatingIntegration;

	SessionService(final IntricIntegration intricIntegration, final TemplatingIntegration templatingIntegration) {
		this.intricIntegration = intricIntegration;
		this.templatingIntegration = templatingIntegration;
	}

	public Session createSession(final Flow flow) {
		var session = new Session(flow);
		sessions.put(session.getId(), session);
		return session;
	}

	public void terminateSession(final UUID sessionId) {
		var session = getSession(sessionId);

		// Extract the id:s of the files uploaded in the session
		var uploadedFileIds = session.getInput().values().stream()
			.flatMap(Collection::stream)
			.map(Input::getIntricFileId)
			.toList();
		// Delete the files
		intricIntegration.deleteFiles(uploadedFileIds);
		// Remove the session
		sessions.remove(sessionId);
	}

	public Session getSession(final UUID sessionId) {
		return ofNullable(sessions.get(sessionId))
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "No session exists with id " + sessionId));
	}

	public Session addInput(final UUID sessionId, final String inputId, final String value) {
		var session = getSession(sessionId);
		session.addInput(inputId, value);
		return session;
	}

	public Session addInput(final UUID sessionId, final String inputId, final MultipartFile inputMultipartFile) {
		var session = getSession(sessionId);
		session.addInput(inputId, inputMultipartFile);
		return session;
	}

	public Session replaceInput(final UUID sessionId, final String inputId, final String value) {
		var session = getSession(sessionId);
		session.replaceInput(inputId, value);
		return session;
	}

	public Session replaceInput(final UUID sessionId, final String inputId, final MultipartFile inputMultipartFile) {
		var session = getSession(sessionId);
		session.replaceInput(inputId, inputMultipartFile);
		return session;
	}

	public StepExecution createStepExecution(final UUID sessionId, final String stepId) {
		var session = getSession(sessionId);
		var flow = session.getFlow();
		var step = getStep(session, stepId);

		// Validate the session input refs
		for (var stepInput : step.getInputs()) {
			if (stepInput instanceof FlowInputRef flowInputRef) {
				// Make sure that the step input is set in the session
				var sessionInput = session.getInput();
				if (sessionInput != null && !sessionInput.containsKey(flowInputRef.getInput())) {
					throw Problem.valueOf(Status.BAD_REQUEST, "Required input '%s' is unset for step '%s' in flow '%s' for session %s".formatted(flowInputRef.getInput(), step.getName(), flow.getName(), sessionId));
				}
			}
		}

		// Validate redirected output inputs
		var requiredStepExecutions = new ArrayList<StepExecution>();
		for (var stepInput : step.getInputs()) {
			if (stepInput instanceof RedirectedOutput redirectedOutput) {
				// Make sure required step(s) have been executed before this one
				var sourceStepId = redirectedOutput.getStep();

				if (session.getStepExecutions() == null || !session.getStepExecutions().containsKey(sourceStepId) || isBlank(session.getStepExecutions().get(sourceStepId).getOutput())) {
					LOG.info("Missing redirected output from step '{}' for step '{}' in flow '{}' for session {}", sourceStepId, stepId, flow.getName(), sessionId);

					requiredStepExecutions.add(createStepExecution(sessionId, sourceStepId));
				}
			}
		}

		// Mark the session as running
		session.setState(Session.State.RUNNING);

		LOG.info("Created step execution for step '{}' from flow '{}' for session {}", step.getName(), flow.getName(), session.getId());

		var stepExecution = new StepExecution(session, step, requiredStepExecutions);
		session.addStepExecution(step.getId(), stepExecution);
		return stepExecution;
	}

	public String renderSession(final UUID sessionId, final String templateId, final String municipalityId) {
		var session = getSession(sessionId);

		return templatingIntegration.renderSession(session, templateId, municipalityId);
	}

	public Step getStep(final Session session, final String stepId) {
		var flow = session.getFlow();

		return flow.getSteps().stream()
			.filter(step -> stepId.equals(step.getId()))
			.findFirst()
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "No step with '%s' exists in flow '%s' for session %s".formatted(stepId, flow.getName(), session.getId())));
	}
}

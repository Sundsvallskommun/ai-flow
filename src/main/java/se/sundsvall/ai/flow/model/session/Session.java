package se.sundsvall.ai.flow.model.session;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import se.sundsvall.ai.flow.model.flowdefinition.RedirectedOutput;
import se.sundsvall.ai.flow.model.flowdefinition.Step;
import se.sundsvall.ai.flow.model.support.StringMultipartFile;
import se.sundsvall.ai.flow.model.support.UploadedMultipartFile;
import se.sundsvall.ai.flow.model.flowdefinition.Flow;
import se.sundsvall.ai.flow.model.flowdefinition.FlowInput;

public class Session {

	private static final Logger LOG = LoggerFactory.getLogger(Session.class);

	public enum State {
		CREATED,
		RUNNING,
		FINISHED
	}

	private final UUID id;
	@JsonIgnore
	private final Flow flow;
	private final Map<String, List<Input>> input = new ConcurrentHashMap<>();
	private final Map<String, StepExecution> stepExecutions = new ConcurrentHashMap<>();
	private State state;

	public Session(final Flow flow) {
		id = UUID.randomUUID();
		state = State.CREATED;

		this.flow = flow;

		// Create initial (empty) executions for all steps
		flow.getSteps().forEach(this::createStepExecution);
	}

	public UUID getId() {
		return id;
	}

	public Flow getFlow() {
		return flow;
	}

	public State getState() {
		return state;
	}

	public void setState(final State state) {
		this.state = state;
	}

	public LocalDateTime getLastUpdatedAt() {
		return stepExecutions.values().stream()
			.filter(Objects::nonNull)
			.map(StepExecution::getLastUpdatedAt)
			.max(LocalDateTime::compareTo)
			.orElse(null);
	}

	public void addInput(final String inputId, final String value) {
		var flowInput = flow.getFlowInput(inputId);
		var inputMultipartFile = new StringMultipartFile(flow.getInputPrefix(), flowInput.getName(), value);

		addOrReplaceInput(flowInput, inputMultipartFile, false);
	}

	public void addInput(final String inputId, final MultipartFile inputMultipartFile) {
		var flowInput = flow.getFlowInput(inputId);
		var uploadedInputMultipartFile = new UploadedMultipartFile(flowInput.getName(), inputMultipartFile);

		addOrReplaceInput(flowInput, uploadedInputMultipartFile, false);
	}

	public void replaceInput(final String inputId, final String value) {
		var flowInput = flow.getFlowInput(inputId);
		var inputMultipartFile = new StringMultipartFile(flow.getInputPrefix(), flowInput.getName(), value);

		addOrReplaceInput(flowInput, inputMultipartFile, true);
	}

	public void replaceInput(final String inputId, final MultipartFile inputMultipartFile) {
		var flowInput = flow.getFlowInput(inputId);
		var uploadedInputMultipartFile = new UploadedMultipartFile(flowInput.getName(), inputMultipartFile);

		addOrReplaceInput(flowInput, uploadedInputMultipartFile, true);
	}

	void addOrReplaceInput(final FlowInput flowInput, final MultipartFile inputMultipartFile, final boolean replace) {
		// Create an empty input value list, if required
		input.computeIfAbsent(flowInput.getId(), ignored -> new LinkedList<>());

		// If we're either replacing the input, or if the flow input is single-valued - replace the
		// previous value by clearing any previous value(s)
		if (replace || flowInput.isSingleValued()) {
			input.get(flowInput.getId()).clear();
		}
		// Add the input
		input.get(flowInput.getId()).add(new Input(inputMultipartFile));
	}

	public Map<String, List<Input>> getInput() {
		return input;
	}

	public Map<String, String> getInputInfo() {
		return input.entrySet().stream()
			.map(entry -> {
				var inputId = entry.getKey();
				// Get the flow input corresponding to this input
				var flowInput = flow.getFlowInput(inputId);
				// Extract the Intric uploaded file ids
				var intricFileIds = entry.getValue().stream().map(Input::getIntricFileId).map(UUID::toString).toList();
				// Format the info
				var info = String.format("Du hittar %s i filen/filerna %s.", flowInput.getName().toLowerCase(), String.join(",", intricFileIds));

				return new AbstractMap.SimpleEntry<>(inputId, info);
			})
			.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	StepExecution createStepExecution(final Step step) {
		// TODO: uncomment and move this to actual session/step execution
		// Validate the session input refs
		/*
		for (var stepInput : step.getInputs()) {
			// Make sure that the step input is set in the session
			if (stepInput instanceof FlowInputRef flowInputRef && !input.containsKey(flowInputRef.getInput())) {
				throw Problem.valueOf(Status.BAD_REQUEST, "Required input '%s' is unset for step '%s' in flow '%s' for session %s".formatted(flowInputRef.getInput(), step.getName(), flow.getName(), id));
			}
		}
		*/

		// Validate redirected output inputs
		var requiredStepExecutions = new ArrayList<StepExecution>();
		for (var stepInput : step.getInputs()) {
			if (stepInput instanceof RedirectedOutput redirectedOutput) {
				// Make sure required step(s) have been executed before this one
				var sourceStepId = redirectedOutput.getStep();
				var sourceStep = flow.getStep(sourceStepId);

				if (!stepExecutions.containsKey(sourceStepId) || isBlank(stepExecutions.get(sourceStepId).getOutput())) {
					LOG.info("Missing redirected output from step '{}' for step '{}' in flow '{}' for session {}", sourceStepId, step.getId(), flow.getName(), id);

					requiredStepExecutions.add(createStepExecution(sourceStep));
				}
			}
		}

		LOG.info("Created step execution for step '{}' from flow '{}' for session {}", step.getName(), flow.getName(), id);

		var stepExecution = new StepExecution(this, step, requiredStepExecutions);
		stepExecutions.put(step.getId(), stepExecution);
		return stepExecution;
	}

	public void addStepExecution(final String stepId, final StepExecution stepExecution) {
		stepExecutions.put(stepId, stepExecution);
	}

	public Map<String, StepExecution> getStepExecutions() {
		return stepExecutions;
	}

	public StepExecution getStepExecution(final String stepId) {
		return of(stepExecutions)
			.map(actualStepExecutions -> actualStepExecutions.get(stepId))
			.orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND, "No step execution exists for step '%s' in session '%s'".formatted(stepId, id)));
	}
}

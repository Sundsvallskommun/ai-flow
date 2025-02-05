package se.sundsvall.ai.flow.model.session;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import se.sundsvall.ai.flow.model.flowdefinition.Step;

import static java.util.Collections.emptyList;

public class StepExecution implements Comparable<StepExecution> {

	public enum State {
		CREATED,
		RUNNING,
		DONE,
		ERROR
	}

	@JsonIgnore
	private final UUID id;
	@JsonIgnore
	private final Session session;
	@JsonIgnore
	private final Step step;
	@JsonIgnore
	private final List<StepExecution> requiredStepExecutions;
	@JsonIgnore
	private UUID intricSessionId;

	private State state;
	private LocalDateTime startedAt;
	private LocalDateTime lastUpdatedAt;
	private LocalDateTime finishedAt;
	private String output;
	private String errorMessage;

	public StepExecution(final Session session, final Step step) {
		this(session, step, emptyList());
	}

	public StepExecution(final Session session, final Step step, final List<StepExecution> requiredStepExecutions) {
		id = UUID.randomUUID();
		state = State.CREATED;

		this.session = session;
		this.step = step;
		this.requiredStepExecutions = requiredStepExecutions;
	}

	public UUID getId() {
		return id;
	}

	public Session getSession() {
		return session;
	}

	public Step getStep() {
		return step;
	}

	public List<StepExecution> getRequiredStepExecutions() {
		return requiredStepExecutions;
	}

	public LocalDateTime getStartedAt() {
		return startedAt;
	}

	public LocalDateTime getLastUpdatedAt() {
		return lastUpdatedAt;
	}

	public LocalDateTime getFinishedAt() {
		return finishedAt;
	}

	public State getState() {
		return state;
	}

	public StepExecution withState(final State state) {
		this.state = state;
		return this;
	}

	public void setState(final State state) {
		this.state = state;

		if (state == State.RUNNING) {
			startedAt = LocalDateTime.now();
		} else if (state == State.DONE || state == State.ERROR) {
			finishedAt = LocalDateTime.now();
		}
		lastUpdatedAt = LocalDateTime.now();
	}

	@JsonIgnore
	public boolean isRunning() {
		return state == State.RUNNING;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(final String output) {
		this.output = output;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public UUID getIntricSessionId() {
		return intricSessionId;
	}

	public void setIntricSessionId(final UUID intricSessionId) {
		this.intricSessionId = intricSessionId;
	}

	@Override
	public int compareTo(final StepExecution other) {
		return startedAt.compareTo(other.startedAt);
	}
}

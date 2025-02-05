package se.sundsvall.ai.flow.model.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import se.sundsvall.ai.flow.model.flowdefinition.Step;

class StepExecutionTest {

	@Test
	void getterAndSetter() {

		var sessionId = UUID.randomUUID();
		var step = new Step();
		List<StepExecution> requiredStepExecutions = List.of();

		var state = StepExecution.State.CREATED;
		var output = "output";
		var errorMessage = "errorMessage";

		var stepExecution = new StepExecution(sessionId, step, requiredStepExecutions);
		stepExecution.setState(state);
		stepExecution.setOutput(output);
		stepExecution.setErrorMessage(errorMessage);

		assertThat(stepExecution.getId()).isNotNull();
		assertThat(stepExecution.getSessionId()).isEqualTo(sessionId);
		assertThat(stepExecution.getStep()).isEqualTo(step);
		assertThat(stepExecution.getRequiredStepExecutions()).isEqualTo(requiredStepExecutions);
		assertThat(stepExecution.getState()).isEqualTo(state);
		assertThat(stepExecution.getOutput()).isEqualTo(output);
		assertThat(stepExecution.getErrorMessage()).isEqualTo(errorMessage);
	}

}

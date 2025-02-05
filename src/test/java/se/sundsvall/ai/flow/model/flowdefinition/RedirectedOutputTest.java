package se.sundsvall.ai.flow.model.flowdefinition;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RedirectedOutputTest {

	@Test
	void getterAndSetters() {
		var step = "step";
		var name = "name";

		var redirectedOutput = new RedirectedOutput();

		redirectedOutput.setStep(step);
		redirectedOutput.setName(name);

		assertThat(redirectedOutput.getStep()).isEqualTo(step);
		assertThat(redirectedOutput.getName()).isEqualTo(name);
		assertThat(redirectedOutput.getType()).isEqualTo(StepInput.Type.STEP_OUTPUT);
	}

	@Test
	void builderPattern() {
		var step = "step";
		var name = "name";

		var redirectedOutput = new RedirectedOutput();

		redirectedOutput.withStep(step).withName(name);

		assertThat(redirectedOutput.getStep()).isEqualTo(step);
		assertThat(redirectedOutput.getName()).isEqualTo(name);
		assertThat(redirectedOutput.getType()).isEqualTo(StepInput.Type.STEP_OUTPUT);
	}

}

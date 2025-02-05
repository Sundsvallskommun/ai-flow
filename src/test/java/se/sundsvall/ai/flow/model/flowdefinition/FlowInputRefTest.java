package se.sundsvall.ai.flow.model.flowdefinition;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FlowInputRefTest {

	@Test
	void setterAndGetter() {
		var input = "input";

		var flowInputRef = new FlowInputRef();

		flowInputRef.setInput(input);

		assertThat(flowInputRef.getInput()).isEqualTo(input);
		assertThat(flowInputRef.getType()).isEqualTo(StepInput.Type.FLOW_INPUT);
	}

	@Test
	void builderPattern() {
		var input = "input";

		var flowInputRef = new FlowInputRef();

		flowInputRef.withInput(input);

		assertThat(flowInputRef.getInput()).isEqualTo(input);
	}
}

package se.sundsvall.ai.flow.model.flowdefinition;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FlowInputTest {

	@Test
	void gettersAndSetters() {
		var id = "id";
		var name = "name";
		var description = "description";
		var type = InputType.STRING;
		var multipleValued = true;
		var passthrough = true;

		var flowInput = new FlowInput();

		flowInput.setId(id);
		flowInput.setName(name);
		flowInput.setDescription(description);
		flowInput.setType(type);
		flowInput.setMultipleValued(multipleValued);
		flowInput.setPassthrough(passthrough);

		assertThat(flowInput.getId()).isEqualTo(id);
		assertThat(flowInput.getName()).isEqualTo(name);
		assertThat(flowInput.getDescription()).isEqualTo(description);
		assertThat(flowInput.getType()).isEqualTo(type);
		assertThat(flowInput.isMultipleValued()).isEqualTo(multipleValued);
		assertThat(flowInput.isPassthrough()).isEqualTo(passthrough);
	}

	@Test
	void builderPattern() {
		var id = "id";
		var name = "name";
		var description = "description";
		var type = InputType.STRING;
		var multipleValued = true;
		var passthrough = true;

		var flowInput = new FlowInput();

		flowInput.withId(id)
			.withName(name)
			.withDescription(description)
			.withType(type)
			.withMultipleValued(multipleValued)
			.withPassthrough(passthrough);

		assertThat(flowInput.getId()).isEqualTo(id);
		assertThat(flowInput.getName()).isEqualTo(name);
		assertThat(flowInput.getDescription()).isEqualTo(description);
		assertThat(flowInput.getType()).isEqualTo(type);
		assertThat(flowInput.isMultipleValued()).isEqualTo(multipleValued);
		assertThat(flowInput.isPassthrough()).isEqualTo(passthrough);

	}

}

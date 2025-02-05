package se.sundsvall.ai.flow.model.flowdefinition;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class FlowTest {

	@Test
	void setterAndGetter() {
		var id = "id";
		var name = "name";
		var description = "description";
		var inputPrefix = "inputPrefix";
		var defaultTemplateId = "defaultTemplateId";
		var inputs = List.of(new FlowInput());
		var steps = List.of(new Step());

		var flow = new Flow();

		flow.setId(id);
		flow.setName(name);
		flow.setDescription(description);
		flow.setInputPrefix(inputPrefix);
		flow.setDefaultTemplateId(defaultTemplateId);
		flow.setFlowInputs(inputs);
		flow.setSteps(steps);

		assertThat(flow.getId()).isEqualTo(id);
		assertThat(flow.getName()).isEqualTo(name);
		assertThat(flow.getDescription()).isEqualTo(description);
		assertThat(flow.getInputPrefix()).isEqualTo(inputPrefix);
		assertThat(flow.getDefaultTemplateId()).isEqualTo(defaultTemplateId);
		assertThat(flow.getFlowInputs()).isEqualTo(inputs);
		assertThat(flow.getSteps()).isEqualTo(steps);
	}

	@Test
	void builderPattern() {
		var id = "id";
		var name = "name";
		var description = "description";
		var inputPrefix = "inputPrefix";
		var defaultTemplateId = "defaultTemplateId";
		var inputs = List.of(new FlowInput());
		var steps = List.of(new Step());

		var flow = new Flow();

		flow.withId(id)
			.withName(name)
			.withDescription(description)
			.withInputPrefix(inputPrefix)
			.withDefaultTemplateId(defaultTemplateId)
			.withFlowInputs(inputs)
			.withSteps(steps);

		assertThat(flow.getId()).isEqualTo(id);
		assertThat(flow.getName()).isEqualTo(name);
		assertThat(flow.getDescription()).isEqualTo(description);
		assertThat(flow.getInputPrefix()).isEqualTo(inputPrefix);
		assertThat(flow.getDefaultTemplateId()).isEqualTo(defaultTemplateId);
		assertThat(flow.getFlowInputs()).isEqualTo(inputs);
		assertThat(flow.getSteps()).isEqualTo(steps);
	}
}

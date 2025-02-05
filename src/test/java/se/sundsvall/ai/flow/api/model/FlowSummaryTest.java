package se.sundsvall.ai.flow.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FlowSummaryTest {

	@Test
	void constructorAndGetter() {
		var id = "id";
		var version = 1;
		var name = "name";
		var description = "description";

		var flowSummary = new FlowSummary(id, version, name, description);

		assertThat(flowSummary.id()).isEqualTo(id);
		assertThat(flowSummary.version()).isEqualTo(version);
		assertThat(flowSummary.name()).isEqualTo(name);
		assertThat(flowSummary.description()).isEqualTo(description);
	}
}

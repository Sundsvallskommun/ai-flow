package se.sundsvall.ai.flow.model.flowdefinition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
	"step-output-ref", "name"
})
public final class RedirectedOutput extends StepInput {

	@JsonProperty(STEP_OUTPUT_REF)
	private String step;
	private String name;

	public RedirectedOutput() {
		super(Type.STEP_OUTPUT);
	}

	public String getStep() {
		return step;
	}

	public RedirectedOutput withStep(final String step) {
		this.step = step;
		return this;
	}

	public void setStep(final String step) {
		this.step = step;
	}

	public String getName() {
		return name;
	}

	public RedirectedOutput withName(final String name) {
		this.name = name;
		return this;
	}

	public void setName(final String name) {
		this.name = name;
	}
}

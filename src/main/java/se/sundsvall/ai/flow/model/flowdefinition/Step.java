package se.sundsvall.ai.flow.model.flowdefinition;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

public class Step implements Comparable<Step> {

	private String id;
	private int order;
	private String name;
	private String description;
	private IntricEndpoint intricEndpoint;
	@JsonProperty("input")
	private List<StepInput> inputs;

	public String getId() {
		return id;
	}

	public Step withId(final String id) {
		this.id = id;
		return this;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public int getOrder() {
		return order;
	}

	public Step withOrder(final int order) {
		this.order = order;
		return this;
	}

	public void setOrder(final int order) {
		this.order = order;
	}

	public String getName() {
		return name;
	}

	public Step withName(final String name) {
		this.name = name;
		return this;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public Step withDescription(final String description) {
		this.description = description;
		return this;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public IntricEndpoint getIntricEndpoint() {
		return intricEndpoint;
	}

	public Step withIntricEndpoint(final IntricEndpoint intricEndpoint) {
		this.intricEndpoint = intricEndpoint;
		return this;
	}

	public void setIntricEndpoint(final IntricEndpoint intricEndpoint) {
		this.intricEndpoint = intricEndpoint;
	}

	public List<StepInput> getInputs() {
		return inputs;
	}

	public Step withInputs(final List<StepInput> inputs) {
		this.inputs = inputs;
		return this;
	}

	public void setInputs(final List<StepInput> inputs) {
		this.inputs = inputs;
	}

	@Override
	public int compareTo(@NotNull final Step other) {
		return Integer.compare(order, other.order);
	}

	public record IntricEndpoint(Type type, UUID id) {

		public enum Type {
			SERVICE,
			ASSISTANT
		}
	}
}

package se.sundsvall.ai.flow.model.flowdefinition;

public class FlowInput {

	private String id;
	private String name;
	private String description;
	private InputType type;
	private boolean multipleValued = false;
	private boolean passthrough = false;

	public String getId() {
		return id;
	}

	public FlowInput withId(final String id) {
		this.id = id;
		return this;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public FlowInput withName(final String name) {
		this.name = name;
		return this;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public FlowInput withDescription(final String description) {
		this.description = description;
		return this;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public InputType getType() {
		return type;
	}

	public FlowInput withType(final InputType type) {
		this.type = type;
		return this;
	}

	public void setType(final InputType type) {
		this.type = type;
	}

	public boolean isSingleValued() {
		return !multipleValued;
	}

	public boolean isMultipleValued() {
		return multipleValued;
	}

	public FlowInput withMultipleValued(final boolean multipleValued) {
		this.multipleValued = multipleValued;
		return this;
	}

	public void setMultipleValued(final boolean multipleValued) {
		this.multipleValued = multipleValued;
	}

	public boolean isPassthrough() {
		return passthrough;
	}

	public FlowInput withPassthrough(final boolean passthrough) {
		this.passthrough = passthrough;
		return this;
	}

	public void setPassthrough(final boolean passthrough) {
		this.passthrough = passthrough;
	}
}

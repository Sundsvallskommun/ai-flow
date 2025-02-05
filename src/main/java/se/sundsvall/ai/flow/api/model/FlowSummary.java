package se.sundsvall.ai.flow.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

public record FlowSummary(

	@Schema(description = "The flow id") String id,
	@Schema(description = "The flow version") Integer version,
	@Schema(description = "The flow name") String name,
	@Schema(description = "The flow description") String description) {}

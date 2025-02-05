package se.sundsvall.ai.flow.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateSessionRequest(

	@NotBlank
	@Schema(description = "Flow id", example = "tjansteskrivelse", requiredMode = Schema.RequiredMode.REQUIRED)
	String flowId,

	@Schema(description = "Flow version", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	Integer version) { }

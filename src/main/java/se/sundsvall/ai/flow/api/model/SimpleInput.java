package se.sundsvall.ai.flow.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SimpleInput(

	@NotBlank
	@Schema(description = "The input value", requiredMode = Schema.RequiredMode.REQUIRED)
	String value) { }

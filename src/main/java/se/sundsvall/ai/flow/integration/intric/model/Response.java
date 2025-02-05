package se.sundsvall.ai.flow.integration.intric.model;

import java.util.UUID;

public record Response(UUID sessionId, String answer) {

	public Response(String answer) {
		this(null, answer);
	}
}

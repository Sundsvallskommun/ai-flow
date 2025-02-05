package se.sundsvall.ai.flow.model.session.exception;

public class ExecutionException extends RuntimeException {

	public ExecutionException(final String message) {
		super(message);
	}

	public ExecutionException(final String message, final Throwable cause) {
		super(message, cause);
	}
}

package se.sundsvall.ai.flow.integration.intric;

import generated.intric.ai.RunService;
import org.springframework.stereotype.Component;
import se.sundsvall.ai.flow.service.flow.ExecutionState;
import se.sundsvall.ai.flow.service.flow.StepExecution;

@Component
public class IntricIntegration {

	public static final String CLIENT_ID = "intric";

	private final IntricClient client;

	IntricIntegration(final IntricClient client) {
		this.client = client;
	}

	public void runService(final StepExecution stepExecution, final String input) {
		try {
			var request = new RunService().input(input);
			var response = client.runService(stepExecution.getStep().getIntricServiceId(), request);

			// Store the output
			stepExecution.setOutput(response.getOutput());
			// Mark the step execution as done
			stepExecution.setState(ExecutionState.DONE);
		} catch (Exception e) {
			// Store the exception
			stepExecution.setErrorMessage(e.getMessage());
			// Mark the step execution as done
			stepExecution.setState(ExecutionState.ERROR);
		}
	}
}

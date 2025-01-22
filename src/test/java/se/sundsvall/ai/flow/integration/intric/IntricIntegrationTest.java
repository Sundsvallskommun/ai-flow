package se.sundsvall.ai.flow.integration.intric;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.ai.flow.TestDataFactory.createStepExecution;

import generated.intric.ai.RunService;
import generated.intric.ai.ServiceOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.ai.flow.service.flow.ExecutionState;

@ExtendWith(MockitoExtension.class)
class IntricIntegrationTest {

	@Mock
	private IntricClient intricClientMock;

	@InjectMocks
	private IntricIntegration integration;

	@Test
	void runService_1() {
		var stepExecution = createStepExecution();
		var serviceId = stepExecution.getStep().getIntricServiceId();
		var input = "input";

		when(intricClientMock.runService(eq(serviceId), any(RunService.class))).thenReturn(new ServiceOutput().output("new output!"));

		integration.runService(stepExecution, input);

		assertThat(stepExecution.getOutput()).isEqualTo("new output!");
		assertThat(stepExecution.getState()).isEqualTo(ExecutionState.DONE);
		verify(intricClientMock).runService(serviceId, new RunService().input(input));
		verifyNoMoreInteractions(intricClientMock);
	}

	@Test
	void runService_2() {
		var stepExecution = createStepExecution();
		var serviceId = stepExecution.getStep().getIntricServiceId();
		var input = "input";

		doThrow(new RuntimeException("error")).when(intricClientMock).runService(eq(serviceId), any(RunService.class));

		integration.runService(stepExecution, input);

		assertThat(stepExecution.getOutput()).isEqualTo("output");
		assertThat(stepExecution.getState()).isEqualTo(ExecutionState.ERROR);
		assertThat(stepExecution.getErrorMessage()).isEqualTo("error");
		verify(intricClientMock).runService(serviceId, new RunService().input(input));
		verifyNoMoreInteractions(intricClientMock);
	}

}

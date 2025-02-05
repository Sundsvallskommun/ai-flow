package se.sundsvall.ai.flow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.ai.flow.TestDataFactory.createNewSession;
import static se.sundsvall.ai.flow.TestDataFactory.createSessionWithStepExecutions;
import static se.sundsvall.ai.flow.TestDataFactory.createStep3;
import static se.sundsvall.ai.flow.TestDataFactory.createStepExecution;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.ai.flow.integration.intric.IntricIntegration;
import se.sundsvall.ai.flow.model.session.StepExecution;

@ExtendWith(MockitoExtension.class)
class ExecutorTest {

	@Mock
	private SessionService sessionServiceMock;

	@Mock
	private IntricIntegration intricIntegrationMock;

	@Captor
	private ArgumentCaptor<String> intricInputCaptor;

	@InjectMocks
	private Executor executor;

	/**
	 * Test scenario where the step have 3 FlowInputRef inputs.
	 */
	@Test
	void executeStepInternal_1() {
		var session = createNewSession();
		var stepExecution = createStepExecution();

		when(sessionServiceMock.getSession(stepExecution.getSessionId())).thenReturn(session);
		doNothing().when(intricIntegrationMock).runService(eq(stepExecution), anyString());

		executor.executeStepInternal(stepExecution);

		assertThat(stepExecution.getState()).isEqualTo(StepExecution.State.RUNNING);
		verify(intricIntegrationMock).runService(eq(stepExecution), intricInputCaptor.capture());
		assertThat(intricInputCaptor.getValue()).isEqualTo("""
			#####Uppdrag:
			value
			#####Förvaltningens input:
			value
			#####Bakgrundsmaterial:
			value
			""");
	}

	/**
	 * Test scenario where the step have 3 FlowInputRef inputs and 1 RedirectOutPut inputs.
	 */
	@Test
	void executeStepInternal_2() {
		var session = createSessionWithStepExecutions();
		var stepExecution = new StepExecution(UUID.randomUUID(), createStep3(), List.of());
		stepExecution.setState(StepExecution.State.CREATED);
		stepExecution.setOutput("");

		when(sessionServiceMock.getSession(stepExecution.getSessionId())).thenReturn(session);
		doNothing().when(intricIntegrationMock).runService(eq(stepExecution), anyString());

		executor.executeStepInternal(stepExecution);

		assertThat(stepExecution.getState()).isEqualTo(StepExecution.State.RUNNING);
		verify(intricIntegrationMock).runService(eq(stepExecution), intricInputCaptor.capture());
		assertThat(intricInputCaptor.getValue()).isEqualTo("""
			#####Uppdrag:
			value
			#####Förvaltningens input:
			value
			#####Bakgrundsmaterial:
			value
			#####Bakgrund:
			output
			""");
	}
}

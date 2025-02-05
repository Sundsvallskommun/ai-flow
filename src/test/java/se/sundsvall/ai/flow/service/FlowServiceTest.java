package se.sundsvall.ai.flow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.ai.flow.TestDataFactory.createFlowEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.ai.flow.TestDataFactory;
import se.sundsvall.ai.flow.integration.db.FlowRepository;
import se.sundsvall.ai.flow.integration.db.model.FlowEntity;
import se.sundsvall.ai.flow.model.flowdefinition.Flow;
import se.sundsvall.ai.flow.model.flowdefinition.exception.FlowException;

@ExtendWith(MockitoExtension.class)
class FlowServiceTest {

	@Mock
	private FlowRepository flowRepositoryMock;

	@Mock
	private ObjectMapper objectMapperMock;

	@InjectMocks
	private FlowService flowService;

	/**
	 * Test scenario where flow is found.
	 */
	@Test
	void getFlowVersion_whenIdAndVersionExists() throws Exception {
		var flowId = "flowId";
		var version = 1;
		var flowEntity = createFlowEntity();

		when(flowRepositoryMock.findById(any(FlowEntity.IdAndVersion.class))).thenReturn(Optional.of(flowEntity));
		when(objectMapperMock.readValue(flowEntity.getContent(), Flow.class)).thenReturn(new Flow());

		var result = flowService.getFlowVersion(flowId, version);

		assertThat(result).isNotNull();

		verify(flowRepositoryMock).findById(new FlowEntity.IdAndVersion(flowId, version));
		verify(objectMapperMock).readValue(flowEntity.getContent(), Flow.class);
		verifyNoMoreInteractions(flowRepositoryMock, objectMapperMock);
	}

	/**
	 * Test scenario where flow is not found and problem is thrown.
	 */
	@Test
	void getFlowVersion_whenIdAndVersionDoesNotExist() {
		var flowId = "flowId";
		var version = 1;

		when(flowRepositoryMock.findById(any(FlowEntity.IdAndVersion.class))).thenReturn(Optional.empty());

		assertThatThrownBy(() -> flowService.getFlowVersion(flowId, version))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", Status.NOT_FOUND)
			.hasFieldOrPropertyWithValue("title", "Not Found")
			.hasFieldOrPropertyWithValue("detail", "No flow found with id %s and version %s".formatted(flowId, version));

		verify(flowRepositoryMock).findById(new FlowEntity.IdAndVersion(flowId, version));
		verifyNoMoreInteractions(flowRepositoryMock);
	}

	@Test
	void getFlows() {
		var flowEntity = createFlowEntity();
		when(flowRepositoryMock.findAll()).thenReturn(List.of(flowEntity));

		var result = flowService.getFlows();

		assertThat(result).hasSize(1);
		assertThat(result.getFirst()).usingRecursiveComparison().isEqualTo(flowService.toFlowSummary(flowEntity));

		verify(flowRepositoryMock).findAll();
		verifyNoMoreInteractions(flowRepositoryMock);
	}

	/**
	 * Test scenario where flow exists and is deleted.
	 */
	@Test
	void deleteFlow_Version_1() {
		var flowId = "flowId";
		var version = 1;

		when(flowRepositoryMock.existsById(any(FlowEntity.IdAndVersion.class))).thenReturn(true);

		flowService.deleteFlowVersion(flowId, version);

		verify(flowRepositoryMock).existsById(new FlowEntity.IdAndVersion(flowId, version));
		verify(flowRepositoryMock).deleteById(new FlowEntity.IdAndVersion(flowId, version));
		verifyNoMoreInteractions(flowRepositoryMock);
	}

	/**
	 * Test scenario where flow does not exist and problem is thrown.
	 */
	@Test
	void deleteFlow_Version_2() {
		var flowId = "flowId";
		var version = 1;

		when(flowRepositoryMock.existsById(any(FlowEntity.IdAndVersion.class))).thenReturn(false);

		assertThatThrownBy(() -> flowService.deleteFlowVersion(flowId, version))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", Status.NOT_FOUND)
			.hasFieldOrPropertyWithValue("title", "Not Found")
			.hasFieldOrPropertyWithValue("detail", "No flow found with id %s and version %s".formatted(flowId, version));

		verify(flowRepositoryMock).existsById(new FlowEntity.IdAndVersion(flowId, version));
		verifyNoMoreInteractions(flowRepositoryMock);
	}

	/**
	 * Test scenario where flow is created with version 2.
	 */
	@Test
	void createFlow_1() throws JsonProcessingException {
		var flow = TestDataFactory.createFlow();

		when(flowRepositoryMock.findMaxVersionById(flow.getId())).thenReturn(Optional.of(1));
		when(objectMapperMock.writeValueAsString(flow)).thenReturn("content");

		var result = flowService.createFlow(flow);

		assertThat(result).isNotNull();
		verify(flowRepositoryMock).findMaxVersionById(flow.getId());
		verify(objectMapperMock).writeValueAsString(flow);
		verify(flowRepositoryMock).save(any());
		verifyNoMoreInteractions(flowRepositoryMock, objectMapperMock);
	}

	/**
	 * Test scenario where flow could not be written as string and problem is thrown.
	 */
	@Test
	void createFlow_2() throws JsonProcessingException {
		var flow = TestDataFactory.createFlow();

		when(flowRepositoryMock.findMaxVersionById(flow.getId())).thenReturn(Optional.of(1));
		when(objectMapperMock.writeValueAsString(flow)).thenThrow(JsonProcessingException.class);

		assertThatThrownBy(() -> flowService.createFlow(flow))
			.isInstanceOf(FlowException.class)
			.hasMessage("Unable to serialize flow instance to JSON");

		verify(flowRepositoryMock).findMaxVersionById(flow.getId());
		verify(objectMapperMock).writeValueAsString(flow);
		verifyNoMoreInteractions(flowRepositoryMock);
	}
}

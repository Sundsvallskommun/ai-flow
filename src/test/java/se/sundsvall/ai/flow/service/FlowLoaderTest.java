package se.sundsvall.ai.flow.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import se.sundsvall.ai.flow.integration.db.FlowRepository;
import se.sundsvall.ai.flow.integration.db.model.FlowEntity;
import se.sundsvall.ai.flow.model.flowdefinition.Flow;

@ExtendWith(MockitoExtension.class)
class FlowLoaderTest {

	@Mock
	private FlowLoader.Configuration mockConfiguration;
	@Mock
	private ObjectMapper mockObjectMapper;
	@Mock
	private FlowRepository mockFlowRepository;

	@InjectMocks
	private FlowLoader flowLoader;

	@Test
	void run() throws Exception {
		var flowId1 = "flow1";
		var flowVersion1 = 123;
		var flowName1 = "Flow 1";
		var flowDescription1 = "Description 1";
		var flowContent1 = "mockContent1";

		var flowId2 = "flow2";
		var flowVersion2 = 456;
		var flowName2 = "Flow 1";
		var flowDescription2 = "Description 1";
		var flowContent2 = "mockContent2";

		var mockFlowResource1 = mock(Resource.class);
		when(mockFlowResource1.getContentAsByteArray()).thenReturn(flowContent1.getBytes(UTF_8));
		when(mockFlowResource1.getFilename()).thenReturn("mockFlow1.json");
		var mockFlowResource2 = mock(Resource.class);
		when(mockFlowResource2.getContentAsByteArray()).thenReturn(flowContent2.getBytes(UTF_8));
		when(mockFlowResource2.getFilename()).thenReturn("mockFlow2.json");

		var mockFlow1 = mock(Flow.class);
		when(mockFlow1.getId()).thenReturn(flowId1);
		when(mockFlow1.getVersion()).thenReturn(flowVersion1);
		when(mockFlow1.getName()).thenReturn(flowName1);
		when(mockFlow1.getDescription()).thenReturn(flowDescription1);

		var mockFlow2 = mock(Flow.class);
		when(mockFlow2.getId()).thenReturn(flowId2);
		when(mockFlow2.getVersion()).thenReturn(flowVersion2);
		when(mockFlow2.getName()).thenReturn(flowName2);
		when(mockFlow2.getDescription()).thenReturn(flowDescription2);

		when(mockConfiguration.getFlowResources()).thenReturn(new Resource[] {
			mockFlowResource1, mockFlowResource2
		});
		when(mockObjectMapper.readValue(flowContent1, Flow.class)).thenReturn(mockFlow1);
		when(mockObjectMapper.readValue(flowContent2, Flow.class)).thenReturn(mockFlow2);
		// One flow that already exists and one that doesn't
		when(mockFlowRepository.existsById(flowId1)).thenReturn(true);
		when(mockFlowRepository.existsById(flowId2)).thenReturn(false);

		flowLoader.run();

		verify(mockFlowResource1).getContentAsByteArray();
		verify(mockFlowResource1).getFilename();
		verifyNoMoreInteractions(mockFlowResource1);

		verify(mockFlow1).getId();
		verify(mockFlow1).getVersion();
		verify(mockFlow1).getName();
		verify(mockFlow1).getDescription();
		verifyNoMoreInteractions(mockFlow1);

		verify(mockFlowResource2).getContentAsByteArray();
		verify(mockFlowResource2).getFilename();
		verifyNoMoreInteractions(mockFlowResource2);

		verify(mockFlow2).getId();
		verify(mockFlow2).getVersion();
		verify(mockFlow2).getName();
		verify(mockFlow2).getDescription();
		verifyNoMoreInteractions(mockFlow2);

		var flowEntityArgumentCaptor = ArgumentCaptor.forClass(FlowEntity.class);

		verify(mockFlowRepository).save(flowEntityArgumentCaptor.capture());

		assertThat(flowEntityArgumentCaptor.getValue()).satisfies(flowEntity -> {
			assertThat(flowEntity.getId()).isEqualTo(flowId2);
			assertThat(flowEntity.getVersion()).isOne();
			assertThat(flowEntity.getName()).isEqualTo(flowName2);
			assertThat(flowEntity.getDescription()).isEqualTo(flowDescription2);
		});
	}
}

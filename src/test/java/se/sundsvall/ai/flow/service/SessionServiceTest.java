package se.sundsvall.ai.flow.service;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.ai.flow.TestDataFactory.createFlow;
import static se.sundsvall.ai.flow.TestDataFactory.createNewSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import se.sundsvall.ai.flow.integration.templating.TemplatingIntegration;
import se.sundsvall.ai.flow.model.session.Session;
import se.sundsvall.ai.flow.model.flowdefinition.Flow;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

	@Mock
	private TemplatingIntegration templatingIntegrationMock;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private SessionService sessionService;

	@Test
	void createSession() throws JsonProcessingException {
		var flow = createFlow();

		when(objectMapper.readValue("content", Flow.class)).thenReturn(flow);

		var session = sessionService.createSession(flow);

		assertThat(session.getFlow()).isEqualTo(flow);

		verifyNoInteractions(templatingIntegrationMock);
	}

	/**
	 * Test scenario where session is found.
	 */
	@Test
	void getSession_1() {
		var flow = createFlow();
		var session = new Session(flow);
		var sessionId = session.getId();
		ReflectionTestUtils.setField(sessionService, "sessions", new ConcurrentHashMap<>(Map.of(sessionId, session)));

		var result = sessionService.getSession(sessionId);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(sessionId);
		verifyNoInteractions(templatingIntegrationMock);
	}

	/**
	 * Test scenario where session is not found.
	 */
	@Test
	void getSession_2() {
		var sessionId = UUID.randomUUID();
		assertThatThrownBy(() -> sessionService.getSession(sessionId))
			.isInstanceOf(Problem.class)
			.hasFieldOrPropertyWithValue("status", Status.NOT_FOUND)
			.hasFieldOrPropertyWithValue("detail", "No session exists with id " + sessionId);
	}

	@Test
	void addInput() {
		var value = Base64.getEncoder().encodeToString("new value".getBytes());
		var sessionId = UUID.randomUUID();
		var session = createNewSession();
		ReflectionTestUtils.setField(sessionService, "sessions", new ConcurrentHashMap<>(Map.of(sessionId, session)));

		assertThat(session.getInput()).doesNotContain(entry("arendenummer", List.of("new value")));
		var result = sessionService.addInput(sessionId, "arendenummer", value);
		assertThat(result.getInput()).contains(entry("arendenummer", List.of("new value")));

		verifyNoInteractions(templatingIntegrationMock);
	}

	@Test
	void replaceInput() {
		var value = Base64.getEncoder().encodeToString("new value".getBytes());
		var sessionId = UUID.randomUUID();
		var session = createNewSession();
		ReflectionTestUtils.setField(sessionService, "sessions", new ConcurrentHashMap<>(Map.of(sessionId, session)));

		var result = sessionService.replaceInput(sessionId, "arendenummer", value);

		assertThat(result.getInput()).contains(entry("arendenummer", List.of("new value")));
		assertThat(result.getInput()).doesNotContain(entry("arendenummer", List.of("value")));
		verifyNoInteractions(templatingIntegrationMock);
	}

	@Test
	void renderSession() {
		var sessionId = UUID.randomUUID();
		var templateId = "templateId";
		var municipalityId = "2281";
		var session = createNewSession();
		ReflectionTestUtils.setField(sessionService, "sessions", new ConcurrentHashMap<>(Map.of(sessionId, session)));
		when(templatingIntegrationMock.renderSession(session, templateId, municipalityId)).thenReturn("rendered");

		var result = sessionService.renderSession(sessionId, templateId, municipalityId);

		assertThat(result).isEqualTo("rendered");
		verify(templatingIntegrationMock).renderSession(session, templateId, municipalityId);
		verifyNoMoreInteractions(templatingIntegrationMock);
	}

	/**
	 * Test scenario where step is found.
	 */
	@Test
	void getStep_1() {
		var sessionId = UUID.randomUUID();
		var stepId = "arendet";
		var session = createNewSession();
		ReflectionTestUtils.setField(sessionService, "sessions", new ConcurrentHashMap<>(Map.of(sessionId, session)));

		var result = sessionService.getStep(sessionId, stepId);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(stepId);
		verifyNoInteractions(templatingIntegrationMock);
	}

	/**
	 * Test scenario where step is not found.
	 */
	@Test
	void getStep_2() {
		var sessionId = UUID.randomUUID();
		var stepId = "bad-step-id";
		var session = createNewSession();
		ReflectionTestUtils.setField(sessionService, "sessions", new ConcurrentHashMap<>(Map.of(sessionId, session)));

		assertThatThrownBy(() -> sessionService.getStep(sessionId, stepId))
			.isInstanceOf(Problem.class)
			.hasFieldOrPropertyWithValue("status", Status.NOT_FOUND)
			.hasFieldOrPropertyWithValue("detail", "No step with '%s' exists in flow '%s' for session %s".formatted(stepId, session.getFlow().getName(), sessionId));
	}
}

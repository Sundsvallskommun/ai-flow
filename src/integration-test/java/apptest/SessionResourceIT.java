package apptest;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.ai.flow.Application;
import se.sundsvall.ai.flow.api.model.Input;
import se.sundsvall.ai.flow.model.Session;
import se.sundsvall.ai.flow.service.SessionService;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(files = "classpath:/SessionResourceIT/", classes = Application.class)
class SessionResourceIT extends AbstractAppTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String PATH = "/" + MUNICIPALITY_ID + "/session";
	private static final String RESPONSE_FILE = "response.json";
	private static final String REQUEST_FILE = "request.json";

	private UUID sessionId;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private WebTestClient webTestClient;

	@BeforeEach
	void setup() {
		sessionService.createSession("tjansteskrivelse");
		var sessions = (Map<UUID, Session>) ReflectionTestUtils.getField(sessionService, "sessions");
		sessionId = sessions.keySet().stream().findFirst().orElseThrow();
	}

	@AfterEach
	void cleanup() {
		ReflectionTestUtils.setField(sessionService, "sessions", new ConcurrentHashMap<>());
	}

	@Test
	void test1_getSession() {
		setupCall()
			.withServicePath(PATH + "/" + sessionId)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test2_createSession() {
		setupCall()
			.withServicePath(uriBuilder -> uriBuilder.path(PATH)
				.queryParam("flowId", "tjansteskrivelse").build())
			.withHttpMethod(POST)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test3_addInput() {
		setupCall()
			.withServicePath(PATH + "/" + sessionId)
			.withRequest(REQUEST_FILE)
			.withHttpMethod(POST)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test4_replaceInput() {
		// Adds an input to the session.
		setupCall()
			.withServicePath(PATH + "/" + sessionId)
			.withRequest(REQUEST_FILE)
			.withHttpMethod(POST)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();

		// Replaces the input in the session.
		setupCall()
			.withServicePath(PATH + "/" + sessionId)
			.withRequest(REQUEST_FILE)
			.withHttpMethod(PUT)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test5_runStep() {
		// Set the required input for the step.
		webTestClient.post().uri(PATH + "/" + sessionId)
			.bodyValue(new Input("uppdraget-till-tjansten", "VGhpcyBpcyBhIGFyZW5kZW51bW1lciB2YWx1ZQ=="))
			.exchange()
			.expectStatus().isOk();

		webTestClient.post().uri(PATH + "/" + sessionId)
			.bodyValue(new Input("forvaltningens-input", "VGhpcyBpcyBhIGFyZW5kZW51bW1lciB2YWx1ZQ=="))
			.exchange()
			.expectStatus().isOk();

		webTestClient.post().uri(PATH + "/" + sessionId)
			.bodyValue(new Input("bakgrundsmaterial",
				"JVBERi0xLjEKJcOiw6MKMSAwIG9iago8PC9UeXBlL0NhdGFsb2cvUGFnZXM8PC9UeXBlL1BhZ2VzL0NvdW50IDEvS2lkc1syIDAgUl0+Pj4+CmVuZG9iagoyIDAgb2JqCjw8L1R5cGUvUGFnZS9QYXJlbnQgMSAwIFIvTWVkaWFCb3hbMCAwIDU5NCA3OTJdL1Jlc291cmNlczw8L0ZvbnQ8PC9GMSAzIDAgUj4+L1Byb2NTZXRbL1BERi9UZXh0XT4+L0NvbnRlbnRzIDQgMCBSPj4KZW5kb2JqCjMgMCBvYmoKPDwvVHlwZS9Gb250L1N1YnR5cGUvVHlwZTEvTmFtZS9GMS9CYXNlRm9udC9IZWx2ZXRpY2E+PgplbmRvYmoKNCAwIG9iago8PC9MZW5ndGggNSAwIFI+PgpzdHJlYW0KQlQKL0YxIDM2IFRmCjEgMCAwIDEgMjU1IDc1MiBUbQo0OCBUTAooIEhlbGxvKScKKFdvcmxkISknCkVUCmVuZHN0cmVhbQplbmRvYmoKNSAwIG9iago3OAplbmRvYmoKeHJlZgowIDYKMDAwMDAwMDAwMCA2NTUzNiBmCjAwMDAwMDAwMTcgMDAwMDAgbgowMDAwMDAwMDk0IDAwMDAwIG4KMDAwMDAwMDIyOCAwMDAwMCBuCjAwMDAwMDAzMDIgMDAwMDAgbgowMDAwMDAwNDI1IDAwMDAwIG4KdHJhaWxlcgo8PC9TaXplIDYvSW5mbyA8PC9DcmVhdGlvbkRhdGUoRDoyMDIzKS9Qcm9kdWNlcihjbWQycGRmKS9UaXRsZShtaW5pLnBkZik+Pi9Sb290IDEgMCBSPj4Kc3RhcnR4cmVmCjQ0NgolJUVPRgoK"))
			.exchange()
			.expectStatus().isOk();

		// Runs the step.
		setupCall()
			.withServicePath(PATH + "/" + sessionId + "/arendet")
			.withHttpMethod(POST)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test6_getStepExecution() {
		// Set the required input for the step.
		webTestClient.post().uri(PATH + "/" + sessionId)
			.bodyValue(new Input("uppdraget-till-tjansten", "VGhpcyBpcyBhIGFyZW5kZW51bW1lciB2YWx1ZQ=="))
			.exchange()
			.expectStatus().isOk();

		webTestClient.post().uri(PATH + "/" + sessionId)
			.bodyValue(new Input("forvaltningens-input", "VGhpcyBpcyBhIGFyZW5kZW51bW1lciB2YWx1ZQ=="))
			.exchange()
			.expectStatus().isOk();

		webTestClient.post().uri(PATH + "/" + sessionId)
			.bodyValue(new Input("bakgrundsmaterial",
				"JVBERi0xLjEKJcOiw6MKMSAwIG9iago8PC9UeXBlL0NhdGFsb2cvUGFnZXM8PC9UeXBlL1BhZ2VzL0NvdW50IDEvS2lkc1syIDAgUl0+Pj4+CmVuZG9iagoyIDAgb2JqCjw8L1R5cGUvUGFnZS9QYXJlbnQgMSAwIFIvTWVkaWFCb3hbMCAwIDU5NCA3OTJdL1Jlc291cmNlczw8L0ZvbnQ8PC9GMSAzIDAgUj4+L1Byb2NTZXRbL1BERi9UZXh0XT4+L0NvbnRlbnRzIDQgMCBSPj4KZW5kb2JqCjMgMCBvYmoKPDwvVHlwZS9Gb250L1N1YnR5cGUvVHlwZTEvTmFtZS9GMS9CYXNlRm9udC9IZWx2ZXRpY2E+PgplbmRvYmoKNCAwIG9iago8PC9MZW5ndGggNSAwIFI+PgpzdHJlYW0KQlQKL0YxIDM2IFRmCjEgMCAwIDEgMjU1IDc1MiBUbQo0OCBUTAooIEhlbGxvKScKKFdvcmxkISknCkVUCmVuZHN0cmVhbQplbmRvYmoKNSAwIG9iago3OAplbmRvYmoKeHJlZgowIDYKMDAwMDAwMDAwMCA2NTUzNiBmCjAwMDAwMDAwMTcgMDAwMDAgbgowMDAwMDAwMDk0IDAwMDAwIG4KMDAwMDAwMDIyOCAwMDAwMCBuCjAwMDAwMDAzMDIgMDAwMDAgbgowMDAwMDAwNDI1IDAwMDAwIG4KdHJhaWxlcgo8PC9TaXplIDYvSW5mbyA8PC9DcmVhdGlvbkRhdGUoRDoyMDIzKS9Qcm9kdWNlcihjbWQycGRmKS9UaXRsZShtaW5pLnBkZik+Pi9Sb290IDEgMCBSPj4Kc3RhcnR4cmVmCjQ0NgolJUVPRgoK"))
			.exchange()
			.expectStatus().isOk();

		// Runs the step, which creates a step execution.
		webTestClient.post().uri(PATH + "/" + sessionId + "/arendet")
			.exchange()
			.expectStatus().isCreated();

		// Retrieves the step execution.
		setupCall()
			.withServicePath(PATH + "/" + sessionId + "/arendet")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test7_generateSessionOutput() {
		setupCall()
			.withServicePath(PATH + "/" + sessionId + "/generate")
			.withRequest(REQUEST_FILE)
			.withHttpMethod(POST)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

}

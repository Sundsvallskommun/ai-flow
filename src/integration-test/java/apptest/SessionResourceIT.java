package apptest;

import static org.awaitility.Awaitility.await;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.ai.flow.Application;
import se.sundsvall.ai.flow.model.session.Session;
import se.sundsvall.ai.flow.model.session.StepExecution;
import se.sundsvall.ai.flow.service.SessionService;
import se.sundsvall.ai.flow.service.Executor;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(files = "classpath:/SessionResourceIT/", classes = Application.class)
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata.sql"
})
class SessionResourceIT extends AbstractAppTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String PATH = "/" + MUNICIPALITY_ID + "/session";
	private static final String RESPONSE_FILE = "response.json";
	private static final String REQUEST_FILE = "request.json";

	private Session session;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private Executor executor;

	@BeforeEach
	void setup() {
		session = sessionService.createSession("Tjänsteskrivelse", 1);
	}

	@Test
	void test1_getSession() {
		setupCall()
			.withServicePath(PATH + "/" + session.getId())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test2_createSession() {
		setupCall()
			.withServicePath(PATH + "/Tjänsteskrivelse/1")
			.withHttpMethod(POST)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test3_addInput() {
		setupCall()
			.withServicePath(PATH + "/" + session.getId())
			.withRequest(REQUEST_FILE)
			.withHttpMethod(POST)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test4_replaceInput() {
		session.addInput("arendenummer", "VGhpcyBpcyBhbiBvcmlnaW5hbCBhcmVuZGVudW1tZXIgdmFsdWU=");

		// Replaces the input in the session.
		setupCall()
			.withServicePath(PATH + "/" + session.getId())
			.withRequest(REQUEST_FILE)
			.withHttpMethod(PUT)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	@DirtiesContext
	void test5_runStep() {
		session.addInput("uppdraget-till-tjansten", "VGhpcyBpcyBhIGFyZW5kZW51bW1lciB2YWx1ZQ==");
		session.addInput("forvaltningens-input", "VGhpcyBpcyBhIGFyZW5kZW51bW1lciB2YWx1ZQ==");
		session.addInput("bakgrundsmaterial",
			"JVBERi0xLjEKJcOiw6MKMSAwIG9iago8PC9UeXBlL0NhdGFsb2cvUGFnZXM8PC9UeXBlL1BhZ2VzL0NvdW50IDEvS2lkc1syIDAgUl0+Pj4+CmVuZG9iagoyIDAgb2JqCjw8L1R5cGUvUGFnZS9QYXJlbnQgMSAwIFIvTWVkaWFCb3hbMCAwIDU5NCA3OTJdL1Jlc291cmNlczw8L0ZvbnQ8PC9GMSAzIDAgUj4+L1Byb2NTZXRbL1BERi9UZXh0XT4+L0NvbnRlbnRzIDQgMCBSPj4KZW5kb2JqCjMgMCBvYmoKPDwvVHlwZS9Gb250L1N1YnR5cGUvVHlwZTEvTmFtZS9GMS9CYXNlRm9udC9IZWx2ZXRpY2E+PgplbmRvYmoKNCAwIG9iago8PC9MZW5ndGggNSAwIFI+PgpzdHJlYW0KQlQKL0YxIDM2IFRmCjEgMCAwIDEgMjU1IDc1MiBUbQo0OCBUTAooIEhlbGxvKScKKFdvcmxkISknCkVUCmVuZHN0cmVhbQplbmRvYmoKNSAwIG9iago3OAplbmRvYmoKeHJlZgowIDYKMDAwMDAwMDAwMCA2NTUzNiBmCjAwMDAwMDAwMTcgMDAwMDAgbgowMDAwMDAwMDk0IDAwMDAwIG4KMDAwMDAwMDIyOCAwMDAwMCBuCjAwMDAwMDAzMDIgMDAwMDAgbgowMDAwMDAwNDI1IDAwMDAwIG4KdHJhaWxlcgo8PC9TaXplIDYvSW5mbyA8PC9DcmVhdGlvbkRhdGUoRDoyMDIzKS9Qcm9kdWNlcihjbWQycGRmKS9UaXRsZShtaW5pLnBkZik+Pi9Sb290IDEgMCBSPj4Kc3RhcnR4cmVmCjQ0NgolJUVPRgoK");

		// Runs the step.
		setupCall()
			.withServicePath(PATH + "/run/" + session.getId() + "/arendet")
			.withHttpMethod(POST)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();

		var stepExecution = session.getStepExecution("arendet");

		await().atMost(Duration.ofSeconds(30)).until(() -> stepExecution.getState() == StepExecution.State.DONE);
	}

	@Test
	@DirtiesContext
	void test6_getStepExecution() {
		var call = setupCall();
		session.addInput("uppdraget-till-tjansten", "VGhpcyBpcyBhIGFyZW5kZW51bW1lciB2YWx1ZQ==");
		session.addInput("forvaltningens-input", "VGhpcyBpcyBhIGFyZW5kZW51bW1lciB2YWx1ZQ==");
		session.addInput("bakgrundsmaterial",
			"JVBERi0xLjEKJcOiw6MKMSAwIG9iago8PC9UeXBlL0NhdGFsb2cvUGFnZXM8PC9UeXBlL1BhZ2VzL0NvdW50IDEvS2lkc1syIDAgUl0+Pj4+CmVuZG9iagoyIDAgb2JqCjw8L1R5cGUvUGFnZS9QYXJlbnQgMSAwIFIvTWVkaWFCb3hbMCAwIDU5NCA3OTJdL1Jlc291cmNlczw8L0ZvbnQ8PC9GMSAzIDAgUj4+L1Byb2NTZXRbL1BERi9UZXh0XT4+L0NvbnRlbnRzIDQgMCBSPj4KZW5kb2JqCjMgMCBvYmoKPDwvVHlwZS9Gb250L1N1YnR5cGUvVHlwZTEvTmFtZS9GMS9CYXNlRm9udC9IZWx2ZXRpY2E+PgplbmRvYmoKNCAwIG9iago8PC9MZW5ndGggNSAwIFI+PgpzdHJlYW0KQlQKL0YxIDM2IFRmCjEgMCAwIDEgMjU1IDc1MiBUbQo0OCBUTAooIEhlbGxvKScKKFdvcmxkISknCkVUCmVuZHN0cmVhbQplbmRvYmoKNSAwIG9iago3OAplbmRvYmoKeHJlZgowIDYKMDAwMDAwMDAwMCA2NTUzNiBmCjAwMDAwMDAwMTcgMDAwMDAgbgowMDAwMDAwMDk0IDAwMDAwIG4KMDAwMDAwMDIyOCAwMDAwMCBuCjAwMDAwMDAzMDIgMDAwMDAgbgowMDAwMDAwNDI1IDAwMDAwIG4KdHJhaWxlcgo8PC9TaXplIDYvSW5mbyA8PC9DcmVhdGlvbkRhdGUoRDoyMDIzKS9Qcm9kdWNlcihjbWQycGRmKS9UaXRsZShtaW5pLnBkZik+Pi9Sb290IDEgMCBSPj4Kc3RhcnR4cmVmCjQ0NgolJUVPRgoK");

		var stepExecution = sessionService.createStepExecution(session.getId(), "arendet");
		executor.executeStep(stepExecution);

		await().atMost(Duration.ofSeconds(30)).until(() -> stepExecution.getState() == StepExecution.State.DONE);

		// Retrieves the step execution.
		call
			.withServicePath(PATH + "/" + session.getId() + "/arendet")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test7_generateSessionOutput() {
		setupCall()
			.withServicePath(PATH + "/" + session.getId() + "/generate")
			.withRequest(REQUEST_FILE)
			.withHttpMethod(POST)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

}

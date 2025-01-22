package se.sundsvall.ai.flow.integration.intric;

import static se.sundsvall.ai.flow.integration.intric.IntricIntegration.CLIENT_ID;

import generated.intric.ai.AskAssistant;
import generated.intric.ai.AskResponse;
import generated.intric.ai.FilePublic;
import generated.intric.ai.RunService;
import generated.intric.ai.ServiceOutput;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
	name = CLIENT_ID,
	configuration = IntricConfiguration.class,
	url = "${integration.intric.base-url}")
@CircuitBreaker(name = CLIENT_ID)
interface IntricClient {

	@PostMapping("/services/{serviceId}/run/")
	ServiceOutput runService(@PathVariable("serviceId") String serviceId, @RequestBody RunService request);

	@PostMapping("/assistants/{assistantId}/sessions/")
	AskResponse askAssistant(@PathVariable("assistantId") String assistantId, @RequestBody AskAssistant request);

	@PostMapping("/assistants/{assistantId}/sessions/{sessionId}/")
	AskResponse askAssistantFollowup(@PathVariable("assistantId") String assistantId, @PathVariable("sessionId") String sessionId, @RequestBody AskAssistant request);

	@PostMapping("/files/")
	ResponseEntity<FilePublic> uploadFile();

	@DeleteMapping("/files/{fileId}")
	ResponseEntity<Void> deleteFile(@PathVariable("fileId") UUID fileId);
}

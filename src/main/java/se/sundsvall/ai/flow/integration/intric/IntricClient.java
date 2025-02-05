package se.sundsvall.ai.flow.integration.intric;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
	name = CLIENT_ID,
	configuration = IntricConfiguration.class,
	url = "${integration.intric.base-url}")
@CircuitBreaker(name = CLIENT_ID)
public interface IntricClient {

	@PostMapping("/services/{serviceId}/run/")
	ServiceOutput runService(@PathVariable("serviceId") UUID serviceId, @RequestBody RunService request);

	@PostMapping("/assistants/{assistantId}/sessions/")
	AskResponse askAssistant(@PathVariable("assistantId") UUID assistantId, @RequestBody AskAssistant request);

	@PostMapping("/assistants/{assistantId}/sessions/{sessionId}/")
	AskResponse askAssistantFollowup(@PathVariable("assistantId") UUID assistantId, @PathVariable("sessionId") UUID sessionId, @RequestBody AskAssistant request);

	@PostMapping(value = "/files/", consumes = MULTIPART_FORM_DATA_VALUE)
	ResponseEntity<FilePublic> uploadFile(@RequestPart(name = "upload_file") MultipartFile file);

	@DeleteMapping("/files/{fileId}/")
	ResponseEntity<Void> deleteFile(@PathVariable("fileId") UUID fileId);
}

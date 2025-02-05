package se.sundsvall.ai.flow.service;

import static java.util.Optional.ofNullable;
import static org.zalando.problem.Status.NOT_FOUND;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import se.sundsvall.ai.flow.api.model.FlowSummary;
import se.sundsvall.ai.flow.integration.db.FlowRepository;
import se.sundsvall.ai.flow.integration.db.model.FlowEntity;
import se.sundsvall.ai.flow.model.flowdefinition.Flow;
import se.sundsvall.ai.flow.model.flowdefinition.exception.FlowException;

@Service
@Transactional
public class FlowService {

	private final ObjectMapper objectMapper;
	private final FlowRepository flowRepository;

	FlowService(final ObjectMapper objectMapper, final FlowRepository flowRepository) {
		this.flowRepository = flowRepository;
		this.objectMapper = objectMapper;
	}

	@Transactional(readOnly = true)
	public Flow getLatestFlowVersion(final String flowId) {
		return flowRepository.findLatestVersionById(flowId)
			.map(flowEntity -> fromJson(flowEntity.getContent()))
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "No flow found with id " + flowId));
	}

	@Transactional(readOnly = true)
	public Flow getFlowVersion(final String flowId, final Integer version) {
		return flowRepository.findById(new FlowEntity.IdAndVersion(flowId, version))
			.map(flowEntity -> fromJson(flowEntity.getContent()))
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "No flow found with id " + flowId + " and version " + version));
	}

	@Transactional(readOnly = true)
	public List<FlowSummary> getFlows() {
		return flowRepository.findAll().stream()
			.map(this::toFlowSummary)
			.toList();
	}

	public void deleteFlow(final String flowId) {
		if (!flowRepository.existsById(flowId)) {
			throw Problem.valueOf(NOT_FOUND, "No flow found with id " + flowId);
		}
		flowRepository.deleteById(flowId);
	}

	public void deleteFlowVersion(final String flowId, final int version) {
		if (!flowRepository.existsById(new FlowEntity.IdAndVersion(flowId, version))) {
			throw Problem.valueOf(NOT_FOUND, "No flow found with id " + flowId + " and version " + version);
		}
		flowRepository.deleteById(new FlowEntity.IdAndVersion(flowId, version));
	}

	public Flow createFlow(final Flow flow) {
		var flowEntity = new FlowEntity();
		var newVersion = flowRepository.findMaxVersionById(flow.getId())
			.map(oldVersion -> oldVersion + 1)
			.orElse(1);

		flow.setVersion(newVersion);

		var content = toJson(flow);

		flowRepository.save(flowEntity
			.withId(flow.getId())
			.withVersion(newVersion)
			.withName(flow.getName())
			.withDescription(flow.getDescription())
			.withContent(content));

		return flow;
	}

	FlowSummary toFlowSummary(final FlowEntity flowEntity) {
		return ofNullable(flowEntity)
			.map(entity -> new FlowSummary(entity.getId(), entity.getVersion(), entity.getName(), entity.getDescription()))
			.orElse(null);
	}

	Flow fromJson(final String json) {
		try {
			return objectMapper.readValue(json, Flow.class);
		} catch (JsonProcessingException e) {
			throw new FlowException("Unable to deserialize flow instance from JSON", e);
		}
	}

	String toJson(final Flow flow) {
		try {
			return objectMapper.writeValueAsString(flow);
		} catch (JsonProcessingException e) {
			throw new FlowException("Unable to serialize flow instance to JSON", e);
		}
	}
}

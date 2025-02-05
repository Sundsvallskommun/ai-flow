package se.sundsvall.ai.flow.model.flowdefinition;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import se.sundsvall.ai.flow.util.StreamUtil;

@JsonDeserialize(using = StepInput.Deserializer.class)
public abstract class StepInput {

	static final String FLOW_INPUT_REF = "flow-input-ref";
	static final String STEP_OUTPUT_REF = "step-output-ref";
	static final String NAME = "name";

	public enum Type {
		FLOW_INPUT,
		STEP_OUTPUT
	}

	@JsonIgnore
	private final Type type;

	protected StepInput(final Type type) {
		this.type = type;
	}

	public final Type getType() {
		return type;
	}

	static class Deserializer extends StdDeserializer<StepInput> {

		Deserializer() {
			super((Class<?>) null);
		}

		@Override
		public StepInput deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
			var root = (JsonNode) parser.getCodec().readTree(parser);

			// Make sure we actually have an object node to work with
			if (!root.isObject()) {
				throw new JsonParseException("Object node expected");
			}

			// Re-map the child nodes as a map from node name to the actual node, for easier access
			var children = StreamUtil.fromIterator(root.fields())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			// Attempt to extract the "flow-input-ref" field
			var flowInputRef = ofNullable(children.get(FLOW_INPUT_REF))
				.map(JsonNode::textValue)
				.orElse(null);
			// Attempt to extract the "step" and "name" fields
			var step = ofNullable(children.get(STEP_OUTPUT_REF))
				.map(JsonNode::textValue)
				.orElse(null);
			var name = ofNullable(children.get(NAME))
				.map(JsonNode::textValue)
				.orElse(null);

			if (isNotBlank(flowInputRef) && isBlank(step) && isBlank(name)) {
				return new FlowInputRef().withInput(flowInputRef);
			} else if (isBlank(flowInputRef) && isNotBlank(step) && isNotBlank(name)) {
				return new RedirectedOutput().withStep(step).withName(name);
			}

			throw new JsonParseException("Unable to parse step input. Either \"%s\" OR (\"%s\" AND \"%s\") should be set".formatted(FLOW_INPUT_REF, STEP_OUTPUT_REF, NAME));
		}
	}
}

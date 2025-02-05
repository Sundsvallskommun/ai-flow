package se.sundsvall.ai.flow.model.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import se.sundsvall.ai.flow.model.flowdefinition.Flow;

class SessionTest {

	@Test
	void setterAndGetter() {
		var state = Session.State.CREATED;
		var flow = new Flow();

		var session = new Session(flow);
		session.setState(state);

		assertThat(session.getState()).isEqualTo(state);
		assertThat(session.getFlow()).isEqualTo(flow);
	}

	@Test
	void builderPattern() {
		var state = Session.State.CREATED;
		var flow = new Flow();

		var session = new Session(flow);

		session.withState(state);

		assertThat(session.getState()).isEqualTo(state);
		assertThat(session.getFlow()).isEqualTo(flow);
	}
}

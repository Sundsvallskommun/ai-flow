package se.sundsvall.ai.flow.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import se.sundsvall.ai.flow.model.flow.Flow;

class SessionTest {

	@Test
	void setterAndGetter() {
		var state = Session.State.CREATED;
		var flow = new Flow();

		var session = new Session();

		session.setState(state);
		session.setFlow(flow);

		assertThat(session.getState()).isEqualTo(state);
		assertThat(session.getFlow()).isEqualTo(flow);
	}

	@Test
	void builderPattern() {
		var state = Session.State.CREATED;
		var flow = new Flow();

		var session = new Session();

		session.withState(state);
		session.withFlow(flow);

		assertThat(session.getState()).isEqualTo(state);
		assertThat(session.getFlow()).isEqualTo(flow);
	}

}

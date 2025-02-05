package se.sundsvall.ai.flow;

import static se.sundsvall.ai.flow.model.flowdefinition.InputType.TEXT;

import java.util.Base64;
import java.util.List;
import java.util.UUID;
import se.sundsvall.ai.flow.api.model.RenderRequest;
import se.sundsvall.ai.flow.integration.db.model.FlowEntity;
import se.sundsvall.ai.flow.model.session.Session;
import se.sundsvall.ai.flow.model.flowdefinition.Flow;
import se.sundsvall.ai.flow.model.flowdefinition.FlowInput;
import se.sundsvall.ai.flow.model.flowdefinition.FlowInputRef;
import se.sundsvall.ai.flow.model.flowdefinition.RedirectedOutput;
import se.sundsvall.ai.flow.model.flowdefinition.Step;
import se.sundsvall.ai.flow.model.flowdefinition.StepInput;
import se.sundsvall.ai.flow.model.session.StepExecution;

public final class TestDataFactory {

	public static Session createNewSession() {
		var flow = createFlow();
		var session = new Session(flow);
		flow.getFlowInputs().forEach(flowInput -> session.addInput(flowInput.getId(), Base64.getEncoder().encodeToString("value".getBytes())));
		return session;
	}

	public static Session createSessionWithStepExecutions() {
		var flow = createFlow();
		var session = new Session(flow);
		flow.getFlowInputs().forEach(flowInput -> session.addInput(flowInput.getId(), Base64.getEncoder().encodeToString("value".getBytes())));
		session.addStepExecution("step", createStepExecution());
		return session;
	}

	public static Flow createFlow() {
		var flow = new Flow();
		flow.setName("Tjänsteskrivelse");
		flow.setDescription("Ett Intric AI-flöde för tjänsteskrivelser");
		flow.setId("tjansteskrivelse");
		flow.setInputPrefix("#####");
		flow.setDefaultTemplateId("ai-mvp.tjansteskrivelse");
		flow.setFlowInputs(List.of(
			createFlowInput1(),
			createFlowInput2(),
			createFlowInput3(),
			createFlowInput4()));
		flow.setSteps(List.of(
			createStep1(),
			createStep2(),
			createStep3()));
		return flow;
	}

	public static StepExecution createStepExecution() {
		var stepExecution = new StepExecution(UUID.randomUUID(), createStep1(), List.of());
		stepExecution.setState(StepExecution.State.CREATED);
		stepExecution.setOutput("output");

		return stepExecution;
	}

	public static FlowInput createFlowInput1() {
		var flowInput = new FlowInput();
		flowInput.setName("Ärendenummer");
		flowInput.setId("arendenummer");
		flowInput.setType(TEXT);
		flowInput.setPassthrough(true);
		return flowInput;
	}

	public static FlowInput createFlowInput2() {
		var flowInput = new FlowInput();
		flowInput.setName("Uppdrag");
		flowInput.setId("uppdraget-till-tjansten");
		flowInput.setType(TEXT);
		return flowInput;
	}

	public static FlowInput createFlowInput3() {
		var flowInput = new FlowInput();
		flowInput.setName("Förvaltningens input");
		flowInput.setId("forvaltningens-input");
		flowInput.setType(TEXT);
		return flowInput;
	}

	public static FlowInput createFlowInput4() {
		var flowInput = new FlowInput();
		flowInput.setName("Bakgrundsmaterial");
		flowInput.setId("bakgrundsmaterial");
		flowInput.setType(TEXT);
		flowInput.setMultipleValued(true);
		return flowInput;
	}

	public static Step createStep1() {
		var step = new Step();
		step.setName("Ärendet");
		step.setId("arendet");
		step.setIntricEndpoint(new Step.IntricEndpoint(Step.IntricEndpoint.Type.SERVICE, "9dda859f-f7cf-4961-9616-cdcb1c8b3d85"));
		step.setOrder(1);
		step.setInputs(List.of(
			createFlowInputRef("uppdraget-till-tjansten"),
			createFlowInputRef("forvaltningens-input"),
			createFlowInputRef("bakgrundsmaterial")));
		return step;
	}

	public static Step createStep2() {
		var step = new Step();
		step.setName("Bakgrund");
		step.setId("bakgrund");
		step.setIntricEndpoint(new Step.IntricEndpoint(Step.IntricEndpoint.Type.SERVICE, "127dd187-b010-42db-a0b4-f413de22963f"));
		step.setOrder(2);
		step.setInputs(List.of(
			createFlowInputRef("uppdraget-till-tjansten"),
			createFlowInputRef("forvaltningens-input"),
			createFlowInputRef("bakgrundsmaterial")));
		return step;
	}

	public static Step createStep3() {
		var step = new Step();
		step.setName("Förvaltningens överväganden");
		step.setId("forvaltningens-overvaganden");
		step.setIntricEndpoint(new Step.IntricEndpoint(Step.IntricEndpoint.Type.SERVICE, "714e598a-7a73-4870-81e5-1b8c9e3897a3"));
		step.setOrder(3);
		step.setInputs(List.of(
			createFlowInputRef("uppdraget-till-tjansten"),
			createFlowInputRef("forvaltningens-input"),
			createFlowInputRef("bakgrundsmaterial"),
			createRedirectedOutput("Bakgrund", "step")));
		return step;
	}

	public static StepInput createFlowInputRef(final String value) {
		return new FlowInputRef().withInput(value);
	}

	public static StepInput createRedirectedOutput(final String name, final String step) {
		return new RedirectedOutput().withName(name).withStep(step);
	}

	public static RenderRequest createRenderRequest() {
		return new RenderRequest("templateId");
	}

	public static FlowEntity createFlowEntity() {
		return new FlowEntity()
			.withId("tjansteskrivelse")
			.withVersion(1)
			.withName("Tjänsteskrivelse")
			.withDescription("Ett Intric AI-flöde för tjänsteskrivelser")
			.withContent("content");
	}

}

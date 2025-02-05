package se.sundsvall.ai.flow.api;

import generated.intric.ai.AskAssistant;
import generated.intric.ai.ModelId;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.ai.flow.integration.intric.IntricClient;
import se.sundsvall.ai.flow.model.support.StringMultipartFile;

@RestController
@RequestMapping("/test")
class TestResource {

	/*
	 * Assistants:
	 * 
	 * Ärendet: d23e4141-4a30-4652-8fce-12cc5febf676
	 * Bakgrund: 0c98433d-a600-4f96-bda4-350d7cd44cca
	 * Förvaltningens överväganden: 00f9705c-e1f4-4115-946f-fbd5cca70701
	 * Styrdokument och juridik: db1d10a8-79a0-4520-8a8f-4a8903247d56
	 * Ekonomisk hållbarhet: 4d44fc9c-dc21-4c77-afec-c7f72538978d
	 * Ekologisk hållbarhet: ecd38735-ca21-4849-86cf-65c1051253a8
	 * Social hållbarhet: d79e6cfb-9a47-4679-a1c2-5e7393005726
	 * Landsbygdssäkring: d5ac8806-1b72-41bd-9deb-f28f556e0e02
	 */

	/*
	 * 
	 * "uppdraget-till-tjansten"
	 * "forvaltningens-input"
	 * "bakgrundsmaterial"
	 * 
	 */

	private IntricClient intricClient;

	TestResource(final IntricClient intricClient) {
		this.intricClient = intricClient;
	}

	//@PostMapping
	ResponseEntity<Void> test() {
		var uppdrag = "Jag vill skriva en tjänsteskrivelse där vi föreslår en ny strategi för informationssäkerhet, cybersäkerhet och dataskydd i Sundsvalls kommun.";
		var forvaltningensInput = "Vi föreslår att vi ska fatta beslut om strategi för informationssäkerhet, cybersäkerhet och dataskydd";
		var bakgrundsmaterial = """
			Strategi för informationssäkerhet, cybersäkerhet och dataskydd

			1)	Bakgrund

			Strategin är fastställd med utgångspunkt i kommunens ledningssystem för informationssäkerhet och Sundsvalls kommuns strategi för en hållbar digital utveckling 2030 och strategi för arbetet med trygghet och säkerhet i Sundsvalls kommun 2020-2023.----footnote1----
			Strategin beskriver på ett övergripande sätt syftet, förutsättningar och mål när det gäller informationssäkerhet, cybersäkerhet och dataskydd. Strategin beskriver också krav och principer samt hur den övergripande styrningen, planeringen och uppföljningen ska ske.\s
			Strategin beskriver också hur den övergripande organisationen för arbetet med informationssäkerhet, cybersäkerhet och dataskydd är ordnad.
			Strategin verkställs genom kommunernas verksamhetsplaner och följs årligen upp i respektive kommuns interna kontrollplaner.

			2)	Syfte och mål

			Huvudsyftet med denna strategi är att utgöra ett övergripande ramverk som ska säkerställa att uppnå Sundsvalls kommuns vision ”Tillsammans släpper vi skaparkraften fri och bygger ett hållbart Sundsvall för alla” och prioriterade områden ”5000 nya jobb, klimatneutralt och socialt hållbart” och Ånge kommuns vision "Vi är en plats för alla, med närhet till natur och gemenskap. Här ger näring och engagemang kraft till en levande bygd."
			I Sundsvalls kommuns strategi för en hållbar digital utveckling, som tar sikte mot år 2030, fastställs att kommunen ska öka förmågan beträffande cybersäkerhet och genom det tryggheten och tilliten till kommunen vilket också gynnar Ånge kommun på samma sätt genom kommunernas nära samarbete.
			Kommunerna ska säkerställa ett övergripande systematiskt informationssäkerhetsarbete som minskar risken för incidenter och skapar ett ökat förtroende för kommunernas tjänster i samhället. Digitaliseringen ska genomsyras av ett fokus på cybersäkerhet både i utvecklingen samt i den dagliga driften och i tillhandahållandet av digitala tjänster till kommunernas målgrupper.
			Informationssäkerhet, cybersäkerhet och dataskydd ska ytterst bidra till att kommunerna kan leverera viktiga samhällsfunktioner och service som är säkra och värnar om den personliga integriteten.\s

			Övergripande principer för arbetet med informationssäkerhet, cybersäkerhet och dataskydd är att:
			--	Utgå från att information är kommunens viktigaste tillgång och integrera systematiskt informationssäkerhetsarbete i våra verksamhetsprocesser
			--	Integrera systematiskt informationssäkerhetsarbete i våra verksamhetsprocesser för att möjliggöra en hållbar digitalisering\s
			--	Kompetens och förmågan inom informationssäkerhetsområdet ska utvecklas liksom förmågan att hantera informationssäkerhetsincidenter.\s
			--	Alla anställda ska ges tillräcklig kunskap om informationssäkerhet för att kunna inhämta, bearbeta och avlämna information inom ramen för de egna arbetsuppgifterna.\s
			--	En god säkerhetskultur ska genomsyra kommunerna. Med detta menas inte bara att medarbetarna har god kunskap om vilka säkerhetsregler som gäller utan att de också använder gott omdöme, samt kritiskt ifrågasätter och rapporterar händelser som kan påverka säkerheten.
			--	Säkerhetskraven för att förhindra olaga intrång ska vara höga främst på grund av den känsliga information som kommunerna hanterar. Säkerhetskraven ska verifieras genom återkommande intrångstester via oberoende extern part.\s
			--	Arbetet ska samordnas på övergripande nivå enligt den etablerade standardserien SS-ISO/IEC 27000 med målet att skapa och upprätthålla ett ledningssystem för informationssäkerhet (LIS).

			Mer detaljerade mål för informationssäkerhet, cybersäkerhet och dataskydd beskrivs i kommunernas verksamhetsplan för informationssäkerhet, cybersäkerhet och dataskydd.

			3)	Roller och ansvar

			Kommunernas nämnder och bolagsstyrelser har det yttersta ansvaret för informationssäkerheten. Grundprincipen att ansvaret för informationssäkerheten följer det ordinarie verksamhetsansvaret: Den som ansvarar för en viss verksamhet är också ansvarig för informationssäkerheten inom berört verksamhetsområde.
			Högsta tjänsteperson i Sundsvall respektive Ånge har det yttersta ansvaret för att respektive kommun följer denna strategi. Högsta tjänsteperson ansvarar också för hanteringen av informationstillgångar och ska tillse att det finns resurser för detta.\s
			Informationssäkerhetsansvarig (CISO) ansvarar för att säkerställa att arbetet med informationssäkerhet, cybersäkerhet och dataskydd sker i enlighet med tillämpliga interna och externa regelverk. CISO ansvarar för att erforderliga processer finns upprättade, för att kontroller, uppföljningar och rapporter hanteras löpande.
			Ansvaret och uppgifter för CISO och för andra roller med särskilt ansvar för informationssäkerhet, cybersäkerhet och dataskydd finns beskrivna i kommunernas verksamhetsplan för informationssäkerhet, cybersäkerhet och dataskydd.

			4)	Informationssäkerhet, cybersäkerhet och dataskydd

			Informationssäkerhet handlar om att skapa och upprätthålla rutiner för att skydda information utifrån fyra aspekter.
			--	Konfidentialitet: att information inte tillgängliggörs eller avslöjas till obehörig\s
			--	Riktighet: att information är korrekt, aktuell och fullständig\s
			--	Tillgänglighet: att information är åtkomlig och användbar när den behövs
			--	Spårbarhet: att i efterhand kunna härleda specifika händelser till objekt

			4.1 Informationssäkerhet – säker informationshantering

			Arbetet med informationssäkerhet ska genomsyra all kommunal verksamhet oavsett om det gäller digitalisering, skydd av personuppgifter, informationssäkerhet i samhällsviktiga verksamheter och tjänster eller utifrån ett säkerhetskyddsperspektiv. Informationssäkerhet handlar framför allt om att förhindra att information läcker, förvrängs och förstörs. Informationssäkerhet handlar om all data, oavsett form. Det innebär att inom informationssäkerhet är det primära syftet att skydda uppgifternas konfidentialitet, riktighet och tillgänglighet. Varje medarbetare har ett ansvar att medverka i informationssäkerhetsarbetet och vid hantering av personuppgifter. \s
			Informationssäkerhetsarbetet innefattar bland annat att;\s
			--	aktivt arbeta med att identifiera risker och hantera säkerhetshot,\s
			--	genomföra insatser för att eliminera eller minimera risker/konsekvenser,\s
			--	genomföra säkerhetstestning och återställningstester,\s
			--	medverka i arbetet med att utarbeta kontinuitetsplaner,
			--	fastställa och kommunicera de informationssäkerhetskrav som gäller inom kommunen och
			--	genomföra utbildningsinsatser.
			--	följa det framtagna ledningssystemet för informationssäkerhet

			4.2 Cybersäkerhet

			Cybersäkerhet handlar om tekniker, metoder och processer för att skydda och försvara digitala tillgångar såsom datorer, servrar, mobila enheter, elektroniska system, nätverk och data från hot, skador, attacker eller obehörig åtkomst.
			Inom cybersäkerhet är det primära syftet att skydda mot obehörig elektronisk åtkomst till data och att arbeta enligt ramverket; identifiera, skydda, upptäcka och återställa.
			Cybersäkerhetsarbetet innefattar bland annat att;
			--	identifiera riskerna för cyberattacker
			--	utveckla en beredskapsplan för att hantera dessa cyberrisker och
			--	utbilda medarbetare i säkerhetsmedvetenhet.

			Det ska finnas ett cybersäkerhetscenter som är en del av Sundsvalls kommuns kommunstyrelsekontor och organisatoriskt placerat under avdelningen för digitalisering och IT. Cybersäkerhetscentret samordnar och hanterar Sundsvall och Ånge kommuns cybersäkerhet.

			4.3 Dataskydd

			Kommunernas förvaltningar, bolag och förbund hanterar personuppgifter i stor mängd som ofta är känsliga ur ett integritetsperspektiv. Det är därför viktigt för kommunernas förtroende att organisationen arbetar aktivt med dataskydd.
			Dataskyddsarbetet innefattar bland annat att:
			--	vid hantering av personuppgifter respekteras enskildas grundläggande rättigheter och friheter, särskilt deras rätt till skydd av personuppgifter
			--	personuppgifter samlas in och behandlas lagligt, rättvist och korrekt
			--	personuppgifter behandlas för uttryckliga och legitima syften
			--	enskilda informeras om hur hans/hennes personuppgifter hanteras
			--	personuppgifter är relevanta och nödvändiga för personuppgiftshanteringen och skyddas med ändamålsenliga organisatoriska och tekniska säkerhetsåtgärder

			5)	Uppföljning och rapportering

			Uppföljning, kvalitetssäkring och dokumentation av arbetet ska ske återkommande och preciseras i planer som så långt som möjligt ska integreras i befintliga rutiner för planering och uppföljning av verksamheten. Uppföljning av kommunernas informationssäkerhetsarbete ska årligen rapporteras i kommunstyrelsen samt i förvaltningsledningarna.\s
			I samband med uppföljningen redogörs bland annat för;\s
			--	trender inom området,
			--	status för arbetet,
			--	övergripande risker och
			--	incidenter\s

			Informationssäkerhetsstrategin revideras var fjärde år. För detta ansvarar informationssäkerhetsansvarig inom kommunen.

			Revisionshistorik

			Datum

			Version

			Ansvarig

			Förändring

			2023-

			1.1

			Informationssäkerhetsansvarig

			Omarbetad strategi

			2023-09-01

			JG

			Diskussionsunderlag JG

			Inför beredningsmöte 2023-09-04\s

			2023-09-04

			HS

			Justeringar HS

			Försökt samskriva kommunerna.

			2023-09-06

			JG

			Kommentarer\s

			Ingen förändring i själva dokumentet\s

			2023-09-11

			CE

			Informationssäkerhetsansvarig

			Ändringar i dokumentet efter kommentarer.

			2023-09-12

			CE

			Informationssäkerhetsansvarig

			Ändringar enl kommentarer.

			2023-09-13

			MaNi

			Kommentarer

			Ingen förändring i själva dokumentet























			footnote1)	 KS-2022-00108-14 Strategi för en hållbar digital utveckling 2030 och KS-2019-00960 Strategi för arbetet med trygghet och säkerhet i Sundsvalls kommun 2020-2023
			""";

		var prefix = "#####";
		var uppdragFile = new StringMultipartFile(prefix, "Uppdrag", uppdrag);
		var forvaltningensInputFile = new StringMultipartFile(prefix, "Förvaltningens input", forvaltningensInput);
		var bakgrundsmaterialFile = new StringMultipartFile(prefix, "Bakgrundsmaterial", bakgrundsmaterial);

		var uppdragFileResponse = intricClient.uploadFile(uppdragFile);
		System.err.println("UPPDRAG ID: " + uppdragFileResponse.getBody().getId());

		var forvaltningensInputFileResponse = intricClient.uploadFile(forvaltningensInputFile);
		System.err.println("FÖRVALTINGENS INPUT ID: " + forvaltningensInputFileResponse.getBody().getId());

		var bakgrundsmaterialFileResponse = intricClient.uploadFile(bakgrundsmaterialFile);
		System.err.println("BAKGRUNDSMATERIAL ID: " + bakgrundsmaterialFileResponse.getBody().getId());

		System.err.println("-----------------------------------------------------------------");

		var askAssistantRequest = new AskAssistant()
			.question("")
			.files(List.of(
				new ModelId().id(uppdragFileResponse.getBody().getId()),
				new ModelId().id(forvaltningensInputFileResponse.getBody().getId()),
				new ModelId().id(bakgrundsmaterialFileResponse.getBody().getId())));
		var assistantId = UUID.fromString("d23e4141-4a30-4652-8fce-12cc5febf676");

		var askAssistantResponse = intricClient.askAssistant(assistantId, askAssistantRequest);
		System.err.println("ANSWER: " + askAssistantResponse.getAnswer());
		System.err.println("SESSION ID: " + askAssistantResponse.getSessionId());

		askAssistantRequest.question("Kan du skriva om texten på engelska?");

		askAssistantResponse = intricClient.askAssistantFollowup(assistantId, askAssistantResponse.getSessionId(), askAssistantRequest);
		System.err.println("ANSWER: " + askAssistantResponse.getAnswer());

		System.err.println("-----------------------------------------------------------------");
		System.err.println("CLEANING UP");

		intricClient.deleteFile(uppdragFileResponse.getBody().getId());
		intricClient.deleteFile(forvaltningensInputFileResponse.getBody().getId());
		intricClient.deleteFile(bakgrundsmaterialFileResponse.getBody().getId());

		System.err.println("-----------------------------------------------------------------");
		System.err.println("DONE");

		return ResponseEntity.ok().build();
	}

	/*
	 * @PostMapping(value = "/files/", consumes = MULTIPART_FORM_DATA_VALUE, produces = ALL_VALUE)
	 * ResponseEntity<Void> uploadFile(@RequestPart final MultipartFile file) {
	 * System.err.println("ORIGINAL FILENAME: " + file.getOriginalFilename());
	 * System.err.println("CONTENT TYPE:      " + file.getContentType());
	 * System.err.println("SIZE:              " + file.getSize());
	 * 
	 * return ResponseEntity.ok().build();
	 * }
	 */

	/*
	@Operation(summary = "Get a session")
	@GetMapping("/{municipalityId}/session/{sessionId}")
	ResponseEntity<Void> getSession(@PathVariable("municipalityId") String municipalityId, @PathVariable("sessionId") String sessionId) {
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Create a new session for a given flow")
	@PostMapping("/{municipalityId}/session")
	ResponseEntity<Void> createSession(@PathVariable("municipalityId") String municipalityId, @Valid @RequestBody final CreateSessionRequest request) {
		return ResponseEntity.ok().build();
	}

	@Operation(summary = " Add simple (text) input to a session")
	@PostMapping(value = "/{municipalityId}/session/{sessionId}/input/{inputId}/simple", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	ResponseEntity<Void> addSimpleInputToSession(@PathVariable("municipalityId") String municipalityId, @PathVariable("sessionId") String sessionId, @PathVariable("inputId") String inputId) {
		return ResponseEntity.ok().build();
	}

	@Operation(summary = " Add binary (file) input to a session")
	@PostMapping(value = "/{municipalityId}/session/{sessionId}/input/{inputId}/file", consumes = MULTIPART_FORM_DATA_VALUE, produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
	ResponseEntity<Void> addFileInputToSession(@PathVariable("municipalityId") String municipalityId, @PathVariable("sessionId") String sessionId, @PathVariable("inputId") String inputId, @RequestPart("file") final MultipartFile inputMultipartFile) {
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Get a step (execution) for a session")
	@GetMapping(value = "/{municipalityId}/session/{sessionId}/step/{stepId}", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	ResponseEntity<Void> getStep(@PathVariable("municipalityId") String municipalityId, @PathVariable("sessionId") String sessionId, @PathVariable("stepId") String stepId) {
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Run/re-run a step (execution) for a session")
	@PostMapping(value = "/{municipalityId}/session/{sessionId}/step/{stepId}", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	ResponseEntity<Void> runStep(@PathVariable("municipalityId") String municipalityId, @PathVariable("sessionId") String sessionId, @PathVariable("stepId") String stepId) {
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Run (all steps in) a session")
	@PostMapping(value = "/{municipalityId}/session/{sessionId}", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	ResponseEntity<Void> runSession(@PathVariable("municipalityId") String municipalityId, @PathVariable("sessionId") String sessionId) {
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Generate the output from the session")
	@PostMapping(value = "/{municipalityId}/session/{sessionId}/generate", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	ResponseEntity<Void> generateSessionOutput(@PathVariable("municipalityId") String municipalityId, @PathVariable("sessionId") String sessionId) {
		return ResponseEntity.ok().build();
	}
	*/
}

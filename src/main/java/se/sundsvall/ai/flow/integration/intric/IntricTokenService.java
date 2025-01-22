package se.sundsvall.ai.flow.integration.intric;

import static java.time.Instant.now;
import static java.util.Optional.ofNullable;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.auth0.jwt.JWT;
import generated.intric.ai.AccessToken;
import java.time.Instant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

@Component
class IntricTokenService {

	private final MultiValueMap<String, String> accessTokenRequestData;
	private final RestClient restClient;

	private Instant tokenExpiration;
	private String token;

	IntricTokenService(final IntricProperties properties) {
		restClient = RestClient.builder()
			.baseUrl(properties.oauth2().tokenUrl())
			.defaultHeader(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
			.defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
				throw Problem.valueOf(Status.INTERNAL_SERVER_ERROR, "Unable to retrieve access token");
			})
			.build();

		accessTokenRequestData = new LinkedMultiValueMap<>();
		accessTokenRequestData.add("grant_type", "password");
		accessTokenRequestData.add("username", properties.oauth2().username());
		accessTokenRequestData.add("password", properties.oauth2().password());
		accessTokenRequestData.add("scope", "");
		accessTokenRequestData.add("client_id", "");
		accessTokenRequestData.add("client_secret", "");
	}

	String getToken() {
		// If we don't have a token at all, or if it's expired - get a new one
		if (token == null || (tokenExpiration != null && tokenExpiration.isBefore(now()))) {
			var tokenResponse = retrieveToken();

			token = ofNullable(tokenResponse.getBody())
				.map(AccessToken::getAccessToken)
				.orElseThrow(() -> Problem.valueOf(Status.INTERNAL_SERVER_ERROR, "Unable to extract access token"));

			// Decode the token to extract the expiresAt instant
			var jwt = JWT.decode(token);
			tokenExpiration = jwt.getExpiresAtAsInstant();
		}

		return token;
	}

	ResponseEntity<AccessToken> retrieveToken() {
		return restClient.post()
			.body(accessTokenRequestData)
			.retrieve()
			.toEntity(AccessToken.class);
	}
}

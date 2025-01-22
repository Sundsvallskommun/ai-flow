package se.sundsvall.ai.flow.integration.intric;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.Instant;
import java.util.List;

import generated.intric.ai.AccessToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class IntricTokenServiceTest {

	@Test
	void constructor() {
		var properties = new IntricProperties("baseUrl", "apiKey",
			new IntricProperties.Oauth2("tokenUrl", "username", "password"), 5, 15);

		var result = new IntricTokenService(properties);

		var accessTokenRequestData = (MultiValueMap<String, String>) ReflectionTestUtils.getField(result, "accessTokenRequestData");

		assertThat(accessTokenRequestData)
			.containsEntry("grant_type", List.of("password"))
			.containsEntry("username", List.of("username"))
			.containsEntry("password", List.of("password"))
			.containsEntry("scope", List.of(""))
			.containsEntry("client_id", List.of(""))
			.containsEntry("client_secret", List.of(""));
		assertThat(ReflectionTestUtils.getField(result, "restClient")).isNotNull();

	}

	/**
	 * Test scenario where the token is null, empty och expired.
	 */
	@Test
	void getToken_1() {
		var properties = new IntricProperties("baseUrl", "apiKey",
			new IntricProperties.Oauth2("tokenUrl", "username", "password"), 5, 15);

		var service = new IntricTokenService(properties);

		var mockClient = Mockito.mock(RestClient.class);
		ReflectionTestUtils.setField(service, "restClient", mockClient, RestClient.class);

		var requestBodyUriSpecMock = Mockito.mock(RestClient.RequestBodyUriSpec.class);
		var requestBodySpecMock = Mockito.mock(RestClient.RequestBodySpec.class);
		var responseSpecMock = Mockito.mock(RestClient.ResponseSpec.class);
		when(mockClient.post()).thenReturn(requestBodyUriSpecMock);
		when(requestBodyUriSpecMock.body(any(MultiValueMap.class))).thenReturn(requestBodySpecMock);
		when(requestBodySpecMock.retrieve()).thenReturn(responseSpecMock);
		when(responseSpecMock.toEntity(AccessToken.class)).thenReturn(ResponseEntity.ok(new AccessToken().tokenType("type").accessToken("token")));

		var jwtMock = mockStatic(JWT.class);
		var decodedJwt = Mockito.mock(DecodedJWT.class);
		when(decodedJwt.getExpiresAtAsInstant()).thenReturn(Instant.MAX);
		jwtMock.when(() -> JWT.decode("token")).thenReturn(decodedJwt);

		var result = service.getToken();

		assertThat(result).isEqualTo("token");
	}

	/**
	 * Test scenario where the token is present and not expired.
	 */
	@Test
	void getToken_2() {
		var properties = new IntricProperties("baseUrl", "apiKey",
			new IntricProperties.Oauth2("tokenUrl", "username", "password"), 5, 15);

		var service = new IntricTokenService(properties);
		ReflectionTestUtils.setField(service, "token", "token", String.class);

		var result = service.getToken();

		assertThat(result).isEqualTo("token");
	}

}

package com.frontegg.sdk.middleware.authenticator;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.config.DefaultConfigProvider;
import com.frontegg.sdk.config.FronteggConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FronteggAuthenticatorTest
{
	private static final String AUTH_TOKEN = "some_token";
	private static final String CLIENT_ID = "client-id";
	private static final String API_KEY = "api-key";
	private static final FronteggConfig config = new DefaultConfigProvider().resolveConfigs();


	public static void setup(ApiClient mockApiClient)
	{
		var authResponse = new AuthResponse();
		authResponse.setToken(AUTH_TOKEN);
		authResponse.setExpiresIn(100);
		var fronteggHttpResponse = new FronteggHttpResponse<AuthResponse>();
		fronteggHttpResponse.setBody(authResponse);

		when(mockApiClient.post(anyString(), eq(AuthResponse.class), any(AuthRequest.class))).thenReturn(
				fronteggHttpResponse);
	}

	@Test
	public void refreshAuthentication_forceRefreshToken(@Mock ApiClient mockApiClient)
	{
		setup(mockApiClient);
		var cut = new FronteggAuthenticator(CLIENT_ID, API_KEY, config, mockApiClient);
		cut.setAccessToken("expired-token"); // Protected accessor
		cut.refreshAuthentication();

		var captor = ArgumentCaptor.forClass(AuthRequest.class);
		verify(mockApiClient, times(1)).post(eq(config.getUrlConfig().getAuthenticationService()),
		                                     eq(AuthResponse.class),
		                                     captor.capture());

		var authRequest = captor.getValue();
		assertEquals(CLIENT_ID, authRequest.getClientId());
		assertEquals(API_KEY, authRequest.getSecret());

		assertEquals(AUTH_TOKEN, cut.getAccessToken());
	}

	@Test
	public void validateAuthentication_missingAccessToken(@Mock ApiClient mockApiClient)
	{
		setup(mockApiClient);
		var cut = new FronteggAuthenticator(CLIENT_ID, API_KEY, config, mockApiClient);
		cut.validateAuthentication();
		assertNotNull(cut.getAccessToken());
	}

	@Test
	public void validateAuthentication_expiredToken(@Mock ApiClient mockApiClient)
	{
		setup(mockApiClient);
		var cut = new FronteggAuthenticator(CLIENT_ID, API_KEY, config, mockApiClient);
		cut.setAccessTokenExpiry(Instant.now().minusSeconds(5000));
		cut.validateAuthentication();

		verify(mockApiClient, times(1)).post(anyString(), eq(AuthResponse.class), any(AuthRequest.class));
	}

	@Test
	public void validateAuthentication_validToken(@Mock ApiClient mockApiClient)
	{
		var cut = new FronteggAuthenticator(CLIENT_ID, API_KEY, config, mockApiClient);
		cut.setAccessTokenExpiry(Instant.now().plusSeconds(15000));
		cut.setAccessToken(AUTH_TOKEN);
		cut.validateAuthentication();

		verify(mockApiClient, times(0)).post(anyString(), eq(AuthResponse.class), any(AuthRequest.class));
	}
}
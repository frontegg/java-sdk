package com.frontegg.sdk.middleware.authenticator;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.config.DefaultConfigProvider;
import com.frontegg.sdk.config.FronteggConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.util.reflection.Whitebox;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class FronteggAuthenticatorTest
{

	private static final String AUTH_TOKEN = "some_token";
	private static final String CLIENT_ID = "client-id";
	private static final String API_KEY = "api-key";
	private FronteggAuthenticator fronteggAuthenticator;
	private ApiClient apiClient;
	private final FronteggConfig config = new DefaultConfigProvider().resolveConfigs();

	@BeforeAll
	public void setUp()
	{
        this.apiClient = mock(ApiClient.class);
        this.fronteggAuthenticator = spy(new FronteggAuthenticator(CLIENT_ID, API_KEY, this.config, this.apiClient));

		AuthResponse authResponse = new AuthResponse();
		authResponse.setToken(AUTH_TOKEN);
		authResponse.setExpiresIn(100l);
		FronteggHttpResponse<AuthResponse> fronteggHttpResponse = new FronteggHttpResponse<>();
		fronteggHttpResponse.setBody(authResponse);

		when(this.apiClient.post(anyString(), eq(AuthResponse.class), any(AuthRequest.class))).thenReturn(
				fronteggHttpResponse);
	}

	@Test
	public void refreshAuthentication_forceRefreshToken()
	{
		Whitebox.setInternalState(this.fronteggAuthenticator, "accessToken", "expired-token");
        this.fronteggAuthenticator.refreshAuthentication();

		ArgumentCaptor<AuthRequest> captor = ArgumentCaptor.forClass(AuthRequest.class);
		verify(this.apiClient, times(1)).post(eq(this.config.getUrlConfig().getAuthenticationService()),
                                              eq(AuthResponse.class),
                                              captor.capture());

		AuthRequest authRequest = captor.getValue();
		assertEquals(CLIENT_ID, authRequest.getClientId());
		assertEquals(API_KEY, authRequest.getSecret());

		assertEquals(AUTH_TOKEN, this.fronteggAuthenticator.getAccessToken());
	}

	@Test
	public void validateAuthentication_missingAccessToken()
	{
		Whitebox.setInternalState(this.fronteggAuthenticator, "accessToken", null);
        this.fronteggAuthenticator.validateAuthentication();

		verify(this.fronteggAuthenticator, times(1)).refreshAuthentication();
	}

	@Test
	public void validateAuthentication_expiredToken()
	{
		Whitebox.setInternalState(this.fronteggAuthenticator, "accessTokenExpiry", Instant.now().minusSeconds(5000));
        this.fronteggAuthenticator.validateAuthentication();

		verify(this.fronteggAuthenticator, times(1)).refreshAuthentication();
	}

	@Test
	public void validateAuthentication_validToken()
	{
		Whitebox.setInternalState(this.fronteggAuthenticator, "accessTokenExpiry", Instant.now().plusSeconds(15000));
		Whitebox.setInternalState(this.fronteggAuthenticator, "accessToken", AUTH_TOKEN);
        this.fronteggAuthenticator.validateAuthentication();

		verify(this.fronteggAuthenticator, times(0)).refreshAuthentication();
	}
}
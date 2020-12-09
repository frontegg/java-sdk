package com.frontegg.sdk.middleware.authenticator;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.config.DefaultConfigProvider;
import com.frontegg.sdk.config.FronteggConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.util.reflection.Whitebox;

import java.time.Instant;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FronteggAuthenticatorTest {

    private FronteggAuthenticator fronteggAuthenticator;
    private ApiClient apiClient;
    private FronteggConfig config = new DefaultConfigProvider().resolveConfigs();

    private static final String AUTH_TOKEN = "some_token";
    private static final String CLIENT_ID = "client-id";
    private static final String API_KEY = "api-key";

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        fronteggAuthenticator = spy(
                new FronteggAuthenticator(CLIENT_ID, API_KEY, config, apiClient)
        );

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(AUTH_TOKEN);
        authResponse.setExpiresIn(100l);
        FronteggHttpResponse<AuthResponse> fronteggHttpResponse = new FronteggHttpResponse<>();
        fronteggHttpResponse.setBody(authResponse);

        when(apiClient.post(
                anyString(),
                eq(AuthResponse.class),
                any(AuthRequest.class))
        ).thenReturn(fronteggHttpResponse);
    }

    @Test
    public void refreshAuthentication_forceRefreshToken() {
        Whitebox.setInternalState(fronteggAuthenticator, "accessToken", "expired-token");
        fronteggAuthenticator.refreshAuthentication();

        ArgumentCaptor<AuthRequest> captor = ArgumentCaptor.forClass(AuthRequest.class);
        verify(apiClient, times(1)).post(
                eq(config.getUrlConfig().getAuthenticationService()),
                eq(AuthResponse.class),
                captor.capture()
        );

        AuthRequest authRequest = captor.getValue();
        assertEquals(CLIENT_ID, authRequest.getClientId());
        assertEquals(API_KEY, authRequest.getSecret());

        assertEquals(AUTH_TOKEN, fronteggAuthenticator.getAccessToken());
    }

    @Test
    public void validateAuthentication_missingAccessToken() {
        Whitebox.setInternalState(fronteggAuthenticator, "accessToken", null);
        fronteggAuthenticator.validateAuthentication();

        verify(fronteggAuthenticator, times(1)).refreshAuthentication();
    }

    @Test
    public void validateAuthentication_expiredToken() {
        Whitebox.setInternalState(fronteggAuthenticator, "accessTokenExpiry", Instant.now().minusSeconds(5000));
        fronteggAuthenticator.validateAuthentication();

        verify(fronteggAuthenticator, times(1)).refreshAuthentication();
    }

    @Test
    public void validateAuthentication_validToken() {
        Whitebox.setInternalState(fronteggAuthenticator, "accessTokenExpiry", Instant.now().plusSeconds(15000));
        Whitebox.setInternalState(fronteggAuthenticator, "accessToken", AUTH_TOKEN);
        fronteggAuthenticator.validateAuthentication();

        verify(fronteggAuthenticator, times(0)).refreshAuthentication();
    }
}
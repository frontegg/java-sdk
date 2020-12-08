package com.frontegg.sdk.middleware.authentication.impl;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.config.DefaultConfigProvider;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authentication.FronteggAuthenticationService;
import com.frontegg.sdk.middleware.authenticator.*;
import com.frontegg.sdk.middleware.identity.FronteggIdentityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.util.reflection.Whitebox;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FronteggAuthenticationServiceTest {

    private FronteggAuthenticator authenticator;
    private ApiClient apiClient;
    private FronteggIdentityService fronteggIdentityService;
    private FronteggAuthenticationService fronteggAuthenticationService;
    private FronteggConfig config = new DefaultConfigProvider().resolveConfigs();
    private static final String BEARER_TOKEN = "my-bearer-token";
    private static final String AUTH_TOKEN = "some_token";

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        authenticator = spy(
                new FronteggAuthenticator(
                        "ClientId",
                        "ApiKey",
                        config,
                        apiClient
                )
        );


        fronteggIdentityService = mock(FronteggIdentityService.class);
        fronteggAuthenticationService = new FronteggAuthenticationServiceImpl(authenticator, fronteggIdentityService);
    }

    @Test
    public void withAuthentication() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("authorization")).thenReturn(BEARER_TOKEN);

        fronteggAuthenticationService.withAuthentication(request);

        verify(fronteggIdentityService, times(1)).verifyToken(eq(BEARER_TOKEN));
    }

    @Test
    public void withAuthentication_emptyToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("authorization")).thenReturn("");

        try {
            fronteggAuthenticationService.withAuthentication(request);
            fail("Expected AuthenticationException as bearer token is missing");
        } catch (AuthenticationException ex) {
            verify(fronteggIdentityService, times(0)).verifyToken(eq(BEARER_TOKEN));
        }
    }

    @Test
    public void withAuthentication_invalidToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("authorization")).thenReturn(BEARER_TOKEN);
        doThrow(new FronteggSDKException("invalid token")).when(fronteggIdentityService).verifyToken(eq(BEARER_TOKEN));
        try {
            fronteggAuthenticationService.withAuthentication(request);
            fail("Expected FronteggSDKException as bearer token is invalid");
        } catch (FronteggSDKException ex) {
            verify(fronteggIdentityService, times(1)).verifyToken(eq(BEARER_TOKEN));
        }
    }

    @Test
    public void authenticateFronteggApplicationIfNeeded_needed() {
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

        fronteggAuthenticationService.authenticateFronteggApplicationIfNeeded();
        ArgumentCaptor<AuthRequest> captor = ArgumentCaptor.forClass(AuthRequest.class);
        verify(apiClient, times(1)).post(
                eq(config.getUrlConfig().getAuthenticationService()),
                eq(AuthResponse.class),
                captor.capture()
        );

        AuthRequest authRequest = captor.getValue();
        assertEquals("ClientId", authRequest.getClientId());
        assertEquals("ApiKey", authRequest.getSecret());

        verify(authenticator, times(1)).authenticate();

        assertEquals(AUTH_TOKEN, authenticator.getAccessToken());
    }

    @Test
    public void authenticateFronteggApplicationIfNeeded_noNeedToAuthenticate() {
        Whitebox.setInternalState(authenticator, "accessToken", "some_token");

        fronteggAuthenticationService.authenticateFronteggApplicationIfNeeded();
        verify(apiClient, times(0)).post(
                anyString(),
                eq(AuthResponse.class),
                any(AuthRequest.class)
        );
    }
}
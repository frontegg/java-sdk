package com.frontegg.sdk.middleware.authenticator;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.config.FronteggConfig;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthClient {

    private IApiClient apiClient;
    private static final Logger logger = LoggerFactory.getLogger(AuthClient.class);

    public AuthClient(IApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public AuthResponse authenticate(String clientId, String apiKey, FronteggConfig config) {
        AuthResponse authResponse;
        try {
            AuthRequest request = new AuthRequest(clientId, apiKey);
            Gson gson = new Gson();
            authResponse = apiClient.post(config.getUrlConfig().getAuthenticationService(), AuthResponse.class, gson.toJson(request)).getBody();
        } catch (Exception ex) {
            logger.error("failed to authenticate with frontegg - " + ex.getMessage(), ex);
            throw new AuthenticationException("Failed to authenticate with frontegg", ex);
        }
        return authResponse;
    }
}

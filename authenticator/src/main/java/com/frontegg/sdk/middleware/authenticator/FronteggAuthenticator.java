package com.frontegg.sdk.middleware.authenticator;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.FronteggConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class FronteggAuthenticator {

    private static final Logger logger = LoggerFactory.getLogger(FronteggAuthenticator.class);
    private String accessToken;
    private LocalDateTime accessTokenExpiry = LocalDateTime.now();
    private String clientId;
    private String  apiKey;
    private AuthClient authClient;
    private FronteggConfig config;

    public FronteggAuthenticator(String clientID, String apiKey, FronteggConfig config, IApiClient client) {
        this.clientId = clientID;
        this.apiKey = apiKey;
        this.config = config;
        authClient = new AuthClient(client);
    }

    public Authentication authenticate() {
        return authenticate(false);
    }

    private Authentication authenticate(boolean force) {
        if (!force && !StringHelper.isBlank(accessToken))
            return new FronteggPrincipal(this.accessToken, this.accessTokenExpiry);

        logger.info("posting authentication request");

        AuthResponse response = authClient.authenticate(clientId, apiKey, config);

        logger.info("authenticated with frontegg");

        // Get the token and the expiration time
        // Save the token
        this.accessToken = response.getToken();
        // Next refresh is when we have only 20% of the sliding window remaining
        long nextRefresh = (long) ((response.getExpiresIn() * 1000) * 0.8);
        this.accessTokenExpiry = LocalDateTime.now().plusSeconds(nextRefresh);

        return new FronteggPrincipal(this.accessToken, this.accessTokenExpiry);
    }

    public Authentication refreshAuthentication() {
        return this.authenticate(true);
    }

    public void validateAuthentication() {
        if (StringHelper.isBlank(this.accessToken) || this.accessTokenExpiry == null || LocalDateTime.now().isAfter(this.accessTokenExpiry)) {
            logger.info("authentication token needs refresh - going to refresh it");
            refreshAuthentication();
        }
    }

    public String getAccessToken() {
        validateAuthentication();
        return accessToken;
    }
}

package com.frontegg.sdk.middleware.authenticator;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.FronteggConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class FronteggAuthenticator {

    private static final Logger logger = LoggerFactory.getLogger(FronteggAuthenticator.class);
    private String accessToken;
    private Instant accessTokenExpiry = Instant.now();
    private String clientId;
    private String  apiKey;
    private AuthClient authClient;
    private FronteggConfig config;

    public FronteggAuthenticator(String clientID,
                                 String apiKey,
                                 FronteggConfig config,
                                 ApiClient client) {
        this.clientId = clientID;
        this.apiKey = apiKey;
        this.config = config;
        authClient = new AuthClient(client);
    }

    public void authenticate() {
        authenticate(false);
    }

    private void authenticate(boolean force) {
        if (!force && !StringHelper.isBlank(accessToken)) {
            logger.info("accessToken is already exists");
            return;
        }

        logger.info("posting authentication request");

        AuthResponse response = authClient.authenticate(clientId, apiKey, config);

        logger.info("authenticated with frontegg");

        // Get the token and the expiration time
        // Save the token
        this.accessToken = response.getToken();
        // Next refresh is when we have only 20% of the sliding window remaining
        long nextRefresh = (long) ((response.getExpiresIn() * 1000) * 0.8);
        this.accessTokenExpiry = Instant.now().plusSeconds(nextRefresh);
    }

    public void refreshAuthentication() {
        this.authenticate(true);
    }

    public void validateAuthentication() {
        if (StringHelper.isBlank(this.accessToken) ||
                this.accessTokenExpiry == null ||
                Instant.now().isAfter(this.accessTokenExpiry)) {
            logger.info("authentication token needs refresh - going to refresh it");
            refreshAuthentication();
        }
    }

    public String getAccessToken() {
        validateAuthentication();
        return accessToken;
    }
}

package com.frontegg.sdk.middleware.authenticator;

import java.time.LocalDateTime;

public class FronteggPrincipal implements FronteggAuthentication {
    private String accessToken;
    private LocalDateTime accessTokenExpiry;

    public FronteggPrincipal(String accessToken, LocalDateTime accessTokenExpiry) {
        this.accessToken = accessToken;
        this.accessTokenExpiry = accessTokenExpiry;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public LocalDateTime getAccessTokenExpiry() {
        return accessTokenExpiry;
    }
}

package com.frontegg.sdk.middleware.authenticator;

public class AuthRequest {
    private String clientId;
    private String secret;

    public AuthRequest(String clientId, String apiKey) {
        this.clientId = clientId;
        this.secret = apiKey;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}

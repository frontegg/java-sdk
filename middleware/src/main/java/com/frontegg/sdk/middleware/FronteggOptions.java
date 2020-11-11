package com.frontegg.sdk.middleware;

import com.frontegg.sdk.middleware.context.FronteggContextResolver;

public class FronteggOptions {
    private String clientId;
    private String apiKey;
    private FronteggContextResolver contextResolver;
    private AuthMiddleware authMiddleware;
    private boolean disableCors;
    private String cookieDomainRewrite;
    private int maxRetries;


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public FronteggContextResolver getContextResolver() {
        return contextResolver;
    }

    public void setContextResolver(FronteggContextResolver contextResolver) {
        this.contextResolver = contextResolver;
    }

    public AuthMiddleware getAuthMiddleware() {
        return authMiddleware;
    }

    public void setAuthMiddleware(AuthMiddleware authMiddleware) {
        this.authMiddleware = authMiddleware;
    }

    public boolean isDisableCors() {
        return disableCors;
    }

    public void setDisableCors(boolean disableCors) {
        this.disableCors = disableCors;
    }

    public String getCookieDomainRewrite() {
        return cookieDomainRewrite;
    }

    public void setCookieDomainRewrite(String cookieDomainRewrite) {
        this.cookieDomainRewrite = cookieDomainRewrite;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
}

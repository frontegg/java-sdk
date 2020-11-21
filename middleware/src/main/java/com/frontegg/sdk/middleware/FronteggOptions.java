package com.frontegg.sdk.middleware;


public class FronteggOptions {
    private String clientId;
    private String apiKey;
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

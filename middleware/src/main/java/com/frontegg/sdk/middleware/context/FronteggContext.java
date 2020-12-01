package com.frontegg.sdk.middleware.context;

import com.frontegg.sdk.middleware.authenticator.Authentication;

public class FronteggContext {

    private String tenantId;
    private String userId;

    private String basePath;

    private Authentication authentication;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public void setFronteggBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getFronteggBasePath() {
        return this.basePath;
    }
}

package com.frontegg.sdk.middleware.context;

public class FronteggContext {

    private String tenantId;
    private String userId;
    private String basePath;

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

    public void setFronteggBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getFronteggBasePath() {
        return this.basePath;
    }
}

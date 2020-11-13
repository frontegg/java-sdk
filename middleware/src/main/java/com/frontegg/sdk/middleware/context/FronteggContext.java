package com.frontegg.sdk.middleware.context;

import com.frontegg.sdk.middleware.model.Permission;

import java.util.List;

public class FronteggContext {

    private String tenantId;
    private String userId;
    private List<Permission> permissions;

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

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}

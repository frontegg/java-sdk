package com.frontegg.sdk.middleware.context;

import com.frontegg.sdk.middleware.authenticator.Authentication;
import com.frontegg.sdk.middleware.permission.model.Permission;

import java.util.List;

public class FronteggContext {

    private String tenantId;
    private String userId;
    private List<Permission> permissions;

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

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }
}

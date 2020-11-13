package com.frontegg.sdk.middleware.model;

public class Permission {

    private String rootPermission;
    private String[] actionPermissions;

    public String getRootPermission() {
        return rootPermission;
    }

    public void setRootPermission(String rootPermission) {
        this.rootPermission = rootPermission;
    }

    public String[] getActionPermissions() {
        return actionPermissions;
    }

    public void setActionPermissions(String[] actionPermissions) {
        this.actionPermissions = actionPermissions;
    }
}

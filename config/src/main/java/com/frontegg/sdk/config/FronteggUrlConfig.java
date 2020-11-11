package com.frontegg.sdk.config;

public class FronteggUrlConfig {
    public static final String DEFAULT_BASE_URL = "https://api.frontegg.com";
    public static final String DEFAULT_AUTH_SERVICE_URL = DEFAULT_BASE_URL + "/vendors/auth/token";
    public static final String DEFAULT_AUDIT_URL = DEFAULT_BASE_URL + "/audits/";
    public static final String DEFAULT_NOTIFICATION_SERVICE_URL = DEFAULT_BASE_URL + "/notification/";
    public static final String DEFAULT_TENANT_SERVICE_URL = DEFAULT_BASE_URL + "/tenants/";
    public static final String DEFAULT_METADATA_SERVICE_URL = DEFAULT_BASE_URL + "/metadata/";
    public static final String DEFAULT_TEAM_SERVICE_URL = DEFAULT_BASE_URL + "/team";
    public static final String DEFAULT_EVENT_SERVICE_URL = DEFAULT_BASE_URL + "/event";
    public static final String DEFAULT_IDENTITY_SERVICE_URL = DEFAULT_BASE_URL + "/identity";


    public static final String PROPERTY_KEY_DEFAULT_BASE_URL = "baseUrl";
    public static final String PROPERTY_KEY_DEFAULT_AUTH_SERVICE = "authenticationService";
    public static final String PROPERTY_KEY_DEFAULT_AUDIT_SERVICE = "auditsService";
    public static final String PROPERTY_KEY_DEFAULT_NOTIFICATION_SERVICE = "notificationService";
    public static final String PROPERTY_KEY_DEFAULT_TENANT_SERVICE = "tenantsService";
    public static final String PROPERTY_KEY_DEFAULT_METADATA_SERVICE = "metadataService";
    public static final String PROPERTY_KEY_DEFAULT_TEAM_SERVICE = "teamService";
    public static final String PROPERTY_KEY_DEFAULT_EVENT_SERVICE = "eventService";
    public static final String PROPERTY_KEY_DEFAULT_IDENTITY_SERVICE = "identityService";

    private String baseUrl;
    private String authenticationService;
    private String auditsService;
    private String notificationService;
    private String tenantsService;
    private String metadataService;
    private String teamService;
    private String eventService;
    private String identityService;

    public static FronteggUrlConfig createDefaults() {
        FronteggUrlConfig urlConfig = new FronteggUrlConfig();
        urlConfig.baseUrl = DEFAULT_BASE_URL;
        urlConfig.authenticationService = DEFAULT_AUTH_SERVICE_URL;
        urlConfig.auditsService = DEFAULT_AUDIT_URL;
        urlConfig.notificationService = DEFAULT_NOTIFICATION_SERVICE_URL;
        urlConfig.tenantsService = DEFAULT_TENANT_SERVICE_URL;
        urlConfig.metadataService = DEFAULT_METADATA_SERVICE_URL;
        urlConfig.teamService = DEFAULT_TEAM_SERVICE_URL;
        urlConfig.eventService = DEFAULT_EVENT_SERVICE_URL;
        urlConfig.identityService = DEFAULT_IDENTITY_SERVICE_URL;
        return urlConfig;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAuthenticationService() {
        return authenticationService;
    }

    public void setAuthenticationService(String authenticationService) {
        this.authenticationService = authenticationService;
    }

    public String getAuditsService() {
        return auditsService;
    }

    public void setAuditsService(String auditsService) {
        this.auditsService = auditsService;
    }

    public String getNotificationService() {
        return notificationService;
    }

    public void setNotificationService(String notificationService) {
        this.notificationService = notificationService;
    }

    public String getTenantsService() {
        return tenantsService;
    }

    public void setTenantsService(String tenantsService) {
        this.tenantsService = tenantsService;
    }

    public String getMetadataService() {
        return metadataService;
    }

    public void setMetadataService(String metadataService) {
        this.metadataService = metadataService;
    }

    public String getTeamService() {
        return teamService;
    }

    public void setTeamService(String teamService) {
        this.teamService = teamService;
    }

    public String getEventService() {
        return eventService;
    }

    public void setEventService(String eventService) {
        this.eventService = eventService;
    }

    public String getIdentityService() {
        return identityService;
    }

    public void setIdentityService(String identityService) {
        this.identityService = identityService;
    }
}

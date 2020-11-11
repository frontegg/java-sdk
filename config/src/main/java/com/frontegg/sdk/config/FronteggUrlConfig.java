package com.frontegg.sdk.config;

public class FronteggUrlConfig {
    private static final String DEFAULT_BASE_URL = "https://api.frontegg.com";
    private static final String DEFAULT_AUTH_SERVICE_URL = DEFAULT_BASE_URL + "/auth/vendor";
    private static final String DEFAULT_AUDIT_URL = DEFAULT_BASE_URL + "/audits";

    private String baseUrl;
    private String authenticationService;
    private String auditsService;
    private String notificationService;
    private String tenantsService;
    private String metadataService;
    private String teamService;
    private String eventService;
    private String identityService;

    /*


    export let baseUrl = process.env.FRONTEGG_API_GATEWAY_URL || "https://api.frontegg.com/";
if (baseUrl.endsWith('/')) {
  baseUrl = baseUrl.slice(0, -1);
}

// tslint:disable-next-line:no-namespace
export namespace config {
  // tslint:disable-next-line:class-name
  export class urls {
    public static authenticationService = process.env.FRONTEGG_AUTHENTICATION_SERVICE_URL || `${baseUrl}/vendors/auth/token`;
    public static auditsService = process.env.FRONTEGG_AUDITS_SERVICE_URL || `${baseUrl}/audits/`;
    public static notificationService = process.env.FRONTEGG_NOTIFICATION_SERVICE_URL || `${baseUrl}/notification/`;
    public static tenantsService = process.env.FRONTEGG_TENANTS_SERVICE_URL || `${baseUrl}/tenants/`;
    public static metadataService = process.env.FRONTEGG_METADATA_SERVICE_URL || `${baseUrl}/metadata/`;
    public static teamService = process.env.FRONTEGG_TEAM_MANAGEMENT_SERVICE_URL || `${baseUrl}/team`;
    public static eventService = process.env.FRONTEGG_EVENT_SERVICE_URL || `${baseUrl}/event`;
    public static identityService = process.env.FRONTEGG_IDENTITY_SERVICE_URL || `${baseUrl}/identity`;
  }
}


     */

    public static FronteggUrlConfig createDefaults() {
        FronteggUrlConfig urlConfig = new FronteggUrlConfig();
        urlConfig.baseUrl = DEFAULT_BASE_URL;
        urlConfig.authenticationService = DEFAULT_AUTH_SERVICE_URL;

        //TODO comp[lete

        return urlConfig;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getAuthenticationService() {
        return authenticationService;
    }

    public String getAuditsService() {
        return auditsService;
    }

    public String getNotificationService() {
        return notificationService;
    }

    public String getTenantsService() {
        return tenantsService;
    }

    public String getMetadataService() {
        return metadataService;
    }

    public String getTeamService() {
        return teamService;
    }

    public String getEventService() {
        return eventService;
    }

    public String getIdentityService() {
        return identityService;
    }
}

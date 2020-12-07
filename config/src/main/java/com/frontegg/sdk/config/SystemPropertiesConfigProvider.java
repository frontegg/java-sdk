package com.frontegg.sdk.config;

import com.frontegg.sdk.common.util.StringHelper;

import static com.frontegg.sdk.config.FronteggUrlConfig.*;

public class SystemPropertiesConfigProvider implements ConfigProvider {

    @Override
    public FronteggConfig resolveConfigs() {
        FronteggUrlConfig urlConfig = new FronteggUrlConfig();
        String baseURL = getSystemVariableAndBuildUrl(null, PROPERTY_KEY_DEFAULT_BASE_URL, DEFAULT_BASE_URL);
        urlConfig.setBaseUrl(baseURL);
        urlConfig.setAuthenticationService(getSystemVariableAndBuildUrl(baseURL, PROPERTY_KEY_DEFAULT_AUTH_SERVICE, DEFAULT_AUTH_SERVICE_URL));
        urlConfig.setAuditsService(getSystemVariableAndBuildUrl(baseURL, PROPERTY_KEY_DEFAULT_AUDIT_SERVICE, DEFAULT_AUDIT_URL));
        urlConfig.setNotificationService(getSystemVariableAndBuildUrl(baseURL, PROPERTY_KEY_DEFAULT_NOTIFICATION_SERVICE, DEFAULT_NOTIFICATION_SERVICE_URL));
        urlConfig.setMetadataService(getSystemVariableAndBuildUrl(baseURL, PROPERTY_KEY_DEFAULT_METADATA_SERVICE, DEFAULT_METADATA_SERVICE_URL));
        urlConfig.setTenantsService(getSystemVariableAndBuildUrl(baseURL, PROPERTY_KEY_DEFAULT_TENANT_SERVICE, DEFAULT_TENANT_SERVICE_URL));
        urlConfig.setTeamService(getSystemVariableAndBuildUrl(baseURL, PROPERTY_KEY_DEFAULT_TEAM_SERVICE, DEFAULT_TEAM_SERVICE_URL));
        urlConfig.setEventService(getSystemVariableAndBuildUrl(baseURL, PROPERTY_KEY_DEFAULT_EVENT_SERVICE, DEFAULT_EVENT_SERVICE_URL));
        urlConfig.setIdentityService(getSystemVariableAndBuildUrl(baseURL, PROPERTY_KEY_DEFAULT_IDENTITY_SERVICE, DEFAULT_IDENTITY_SERVICE_URL));

        FronteggConfig fronteggConfig = new FronteggConfig();
        fronteggConfig.setUrlConfig(urlConfig);
        return fronteggConfig;
    }

    private String getSystemVariableAndBuildUrl(String baseUrl, String key, String defaultValue) {
        String val = System.getProperty(key);
        if (StringHelper.isBlank(val, true)) return defaultValue;

        return baseUrl == null ? val : baseUrl+ val;
    }
}

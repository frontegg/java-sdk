package com.frontegg.sdk.middleware.spring.config;

import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.ConfigProvider;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.config.FronteggUrlConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.frontegg.sdk.config.FronteggUrlConfig.*;

@Order(4)
@Component
public class SpringFronteggConfigProvider implements ConfigProvider {

    @Value("${frontegg.config.urls.baseUrl:#{''}}")
    private String baseUrl;
    @Value("${frontegg.config.urls.authenticationService:#{''}}")
    private String authenticationService;
    @Value("${frontegg.config.urls.auditsService:#{''}}")
    private String auditsService;
    @Value("${frontegg.config.urls.notificationService:#{''}}")
    private String notificationService;
    @Value("${frontegg.config.urls.tenantsService:#{''}}")
    private String tenantsService;
    @Value("${frontegg.config.urls.metadataService:#{''}}")
    private String metadataService;
    @Value("${frontegg.config.urls.teamService:#{''}}")
    private String teamService;
    @Value("${frontegg.config.urls.eventService:#{''}}")
    private String eventService;
    @Value("${frontegg.config.urls.identityService:#{''}}")
    private String identityService;


    @Override
    public FronteggConfig resolveConfigs() {
        FronteggUrlConfig urlConfig = new FronteggUrlConfig();
        baseUrl = StringHelper.isBlank(baseUrl, true) ? DEFAULT_BASE_URL : baseUrl;
        urlConfig.setBaseUrl(baseUrl);
        urlConfig.setAuthenticationService(StringHelper.isBlank(authenticationService, true) ? DEFAULT_AUTH_SERVICE_URL : baseUrl + authenticationService);
        urlConfig.setAuditsService(StringHelper.isBlank(authenticationService, true) ? DEFAULT_AUDIT_URL : baseUrl + auditsService);
        urlConfig.setNotificationService(StringHelper.isBlank(notificationService, true) ? DEFAULT_NOTIFICATION_SERVICE_URL : baseUrl + notificationService);
        urlConfig.setMetadataService(StringHelper.isBlank(metadataService, true) ? DEFAULT_TENANT_SERVICE_URL : baseUrl + metadataService);
        urlConfig.setTenantsService(StringHelper.isBlank(tenantsService, true) ? DEFAULT_METADATA_SERVICE_URL : baseUrl + tenantsService);
        urlConfig.setTeamService(StringHelper.isBlank(teamService, true) ? DEFAULT_TEAM_SERVICE_URL : baseUrl + teamService);
        urlConfig.setEventService(StringHelper.isBlank(eventService, true) ? DEFAULT_EVENT_SERVICE_URL : baseUrl + eventService);
        urlConfig.setIdentityService(StringHelper.isBlank(identityService, true) ? DEFAULT_IDENTITY_SERVICE_URL : baseUrl + identityService);

        FronteggConfig fronteggConfig = new FronteggConfig();
        fronteggConfig.setUrlConfig(urlConfig);
        return fronteggConfig;
    }
}

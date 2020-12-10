package com.frontegg.sdk.spring.middleware.config;

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
public class SpringFronteggConfigProvider implements ConfigProvider
{
	@Value("${frontegg.config.urls.baseUrl:#{''}}") private String baseUrl;
	@Value("${frontegg.config.urls.authenticationService:#{''}}") private String authenticationService;
	@Value("${frontegg.config.urls.auditsService:#{''}}") private String auditsService;
	@Value("${frontegg.config.urls.notificationService:#{''}}") private String notificationService;
	@Value("${frontegg.config.urls.tenantsService:#{''}}") private String tenantsService;
	@Value("${frontegg.config.urls.metadataService:#{''}}") private String metadataService;
	@Value("${frontegg.config.urls.teamService:#{''}}") private String teamService;
	@Value("${frontegg.config.urls.eventService:#{''}}") private String eventService;
	@Value("${frontegg.config.urls.identityService:#{''}}") private String identityService;


	@Override
	public FronteggConfig resolveConfigs()
	{
		FronteggUrlConfig urlConfig = new FronteggUrlConfig();
		this.baseUrl = StringHelper.isBlank(this.baseUrl, true) ? DEFAULT_BASE_URL : this.baseUrl;
		urlConfig.setBaseUrl(this.baseUrl);
		urlConfig.setAuthenticationService(StringHelper.isBlank(this.authenticationService, true)
												   ? DEFAULT_AUTH_SERVICE_URL
												   : this.baseUrl + this.authenticationService);
		urlConfig.setAuditsService(StringHelper.isBlank(this.authenticationService, true)
										   ? DEFAULT_AUDIT_URL
										   : this.baseUrl + this.auditsService);
		urlConfig.setNotificationService(StringHelper.isBlank(this.notificationService, true)
												 ? DEFAULT_NOTIFICATION_SERVICE_URL
												 : this.baseUrl + this.notificationService);
		urlConfig.setMetadataService(StringHelper.isBlank(this.metadataService, true)
											 ? DEFAULT_TENANT_SERVICE_URL
											 : this.baseUrl + this.metadataService);
		urlConfig.setTenantsService(StringHelper.isBlank(this.tenantsService, true)
											? DEFAULT_METADATA_SERVICE_URL
											: this.baseUrl + this.tenantsService);
		urlConfig.setTeamService(StringHelper.isBlank(this.teamService, true)
										 ? DEFAULT_TEAM_SERVICE_URL
										 : this.baseUrl + this.teamService);
		urlConfig.setEventService(StringHelper.isBlank(this.eventService, true)
										  ? DEFAULT_EVENT_SERVICE_URL
										  : this.baseUrl + this.eventService);
		urlConfig.setIdentityService(StringHelper.isBlank(this.identityService, true)
											 ? DEFAULT_IDENTITY_SERVICE_URL
											 : this.baseUrl + this.identityService);

		FronteggConfig fronteggConfig = new FronteggConfig();
		fronteggConfig.setUrlConfig(urlConfig);
		return fronteggConfig;
	}
}

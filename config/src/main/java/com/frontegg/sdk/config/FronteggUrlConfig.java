package com.frontegg.sdk.config;

public class FronteggUrlConfig
{
	public static final String DEFAULT_BASE_URL = "https://api.frontegg.com";
	public static final String AUTH_SERVICE_URL = "/auth/vendor";
	public static final String AUDITS_SERVICE_URL = "/audits";
	public static final String TENANT_SERVICE_URL = "/tenants";
	public static final String METADATA_SERVICE_URL = "/metadata";
	public static final String TEAM_SERVICE_URL = "/team";
	public static final String EVENT_SERVICE_URL = "/event";
	public static final String IDENTITY_SERVICE_URL = "/identity";


	public static final String BASE_URL_PROPERTY_KEY = "baseUrl";

	private String baseUrl;
	private String authenticationService;
	private String auditsService;
	private String tenantsService;
	private String metadataService;
	private String teamService;
	private String eventService;
	private String identityService;

	public static FronteggUrlConfig createDefaults()
	{
		var urlConfig = new FronteggUrlConfig();
		urlConfig.baseUrl = DEFAULT_BASE_URL;
		urlConfig.authenticationService = AUTH_SERVICE_URL;
		urlConfig.auditsService = AUDITS_SERVICE_URL;
		urlConfig.tenantsService = TENANT_SERVICE_URL;
		urlConfig.metadataService = METADATA_SERVICE_URL;
		urlConfig.teamService = TEAM_SERVICE_URL;
		urlConfig.eventService = EVENT_SERVICE_URL;
		urlConfig.identityService = IDENTITY_SERVICE_URL;
		return urlConfig;
	}

	public String getBaseUrl()
	{
		return this.baseUrl;
	}

	public void setBaseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	public String getAuthenticationService()
	{
		return this.authenticationService;
	}

	public void setAuthenticationService(String authenticationService)
	{
		this.authenticationService = authenticationService;
	}

	public String getAuditsService()
	{
		return this.auditsService;
	}

	public void setAuditsService(String auditsService)
	{
		this.auditsService = auditsService;
	}

	public String getTenantsService()
	{
		return this.tenantsService;
	}

	public void setTenantsService(String tenantsService)
	{
		this.tenantsService = tenantsService;
	}

	public String getMetadataService()
	{
		return this.metadataService;
	}

	public void setMetadataService(String metadataService)
	{
		this.metadataService = metadataService;
	}

	public String getTeamService()
	{
		return this.teamService;
	}

	public void setTeamService(String teamService)
	{
		this.teamService = teamService;
	}

	public String getEventService()
	{
		return this.eventService;
	}

	public void setEventService(String eventService)
	{
		this.eventService = eventService;
	}

	public String getIdentityService()
	{
		return this.identityService;
	}

	public void setIdentityService(String identityService)
	{
		this.identityService = identityService;
	}
}

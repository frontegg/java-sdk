package com.frontegg.sdk.config;

import java.net.URI;
import java.net.URISyntaxException;

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
		try
		{
			urlConfig.baseUrl = DEFAULT_BASE_URL;
			var auth = new URI(urlConfig.baseUrl).parseServerAuthority().resolve(AUTH_SERVICE_URL);
			var audits = new URI(urlConfig.baseUrl).parseServerAuthority().resolve(AUDITS_SERVICE_URL);
			var metadata = new URI(urlConfig.baseUrl).parseServerAuthority().resolve(METADATA_SERVICE_URL);
			var tenants = new URI(urlConfig.baseUrl).parseServerAuthority().resolve(TENANT_SERVICE_URL);
			var team = new URI(urlConfig.baseUrl).parseServerAuthority().resolve(TEAM_SERVICE_URL);
			var events = new URI(urlConfig.baseUrl).parseServerAuthority().resolve(EVENT_SERVICE_URL);
			var identity = new URI(urlConfig.baseUrl).parseServerAuthority().resolve(IDENTITY_SERVICE_URL);

			urlConfig.setAuthenticationService(auth.toString());
			urlConfig.setAuditsService(audits.toString());
			urlConfig.setMetadataService(metadata.toString());
			urlConfig.setTenantsService(tenants.toString());
			urlConfig.setTeamService(team.toString());
			urlConfig.setEventService(events.toString());
			urlConfig.setIdentityService(identity.toString());
		}
		catch (URISyntaxException e)
		{
			throw new RuntimeException(e);
		}
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

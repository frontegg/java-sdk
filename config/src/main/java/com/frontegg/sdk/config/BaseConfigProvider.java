package com.frontegg.sdk.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static com.frontegg.sdk.config.FronteggUrlConfig.*;

public abstract class BaseConfigProvider implements ConfigProvider
{
	@Override
	public FronteggConfig resolveConfigs()
	{
		var urlConfig = new FronteggUrlConfig();
		var baseURL = Optional.ofNullable(this.getBaseUrl(BASE_URL_PROPERTY_KEY)).orElse(DEFAULT_BASE_URL);
		urlConfig.setBaseUrl(baseURL);

		try
		{
			var auth = new URI(baseURL).parseServerAuthority().resolve(AUTH_SERVICE_URL);
			var audits = new URI(baseURL).parseServerAuthority().resolve(AUDITS_SERVICE_URL);
			var metadata = new URI(baseURL).parseServerAuthority().resolve(METADATA_SERVICE_URL);
			var tenants = new URI(baseURL).parseServerAuthority().resolve(TENANT_SERVICE_URL);
			var team = new URI(baseURL).parseServerAuthority().resolve(TEAM_SERVICE_URL);
			var events = new URI(baseURL).parseServerAuthority().resolve(EVENT_SERVICE_URL);
			var identity = new URI(baseURL).parseServerAuthority().resolve(IDENTITY_SERVICE_URL);

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

		var fronteggConfig = new FronteggConfig();
		fronteggConfig.setUrlConfig(urlConfig);
		return fronteggConfig;
	}

	protected abstract String getBaseUrl(String key);
}

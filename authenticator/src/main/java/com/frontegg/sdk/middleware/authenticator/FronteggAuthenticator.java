package com.frontegg.sdk.middleware.authenticator;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.FronteggConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class FronteggAuthenticator
{
	private static final Logger logger = LoggerFactory.getLogger(FronteggAuthenticator.class);
	private final String clientId;
	private final String apiKey;
	private final AuthClient authClient;
	private final FronteggConfig config;
	private String accessToken;
	private Instant accessTokenExpiry;

	public FronteggAuthenticator(String clientID, String apiKey, FronteggConfig config, ApiClient client)
	{
		this.clientId = clientID;
		this.apiKey = apiKey;
		this.config = config;
		this.authClient = new AuthClient(client);
		this.accessTokenExpiry = Instant.now();
	}

	public void authenticate()
	{
		authenticate(false);
	}

	private void authenticate(boolean force)
	{
		if (!force && !StringHelper.isBlank(this.accessToken))
		{
			return;
		}

		logger.info("Authenticating with frontegg");
		var response = this.authClient.authenticate(this.clientId, this.apiKey, this.config);
		this.accessToken = response.getToken();
		long nextRefresh = (long) (response.getExpiresIn() * 0.8); // refresh after 80% of the expiration time
		this.accessTokenExpiry = Instant.now().plusSeconds(nextRefresh);
	}

	public void refreshAuthentication()
	{
		logger.info("Refreshing token");
		this.authenticate(true);
	}

	public void validateAuthentication()
	{
		if (StringHelper.isBlank(this.accessToken) ||
		    this.accessTokenExpiry == null ||
		    Instant.now().isAfter(this.accessTokenExpiry))
		{
			refreshAuthentication();
		}
	}

	public String getAccessToken()
	{
		validateAuthentication();
		return this.accessToken;
	}

	void setAccessToken(String accessToken)
	{
		this.accessToken = accessToken;
	}

	void setAccessTokenExpiry(Instant expiry)
	{
		this.accessTokenExpiry = expiry;
	}
}

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
	private Instant accessTokenExpiry = Instant.now();

	public FronteggAuthenticator(String clientID, String apiKey, FronteggConfig config, ApiClient client)
	{
		this.clientId = clientID;
		this.apiKey = apiKey;
		this.config = config;
		this.authClient = new AuthClient(client);
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

		logger.info("posting authentication request");

		AuthResponse response = this.authClient.authenticate(this.clientId, this.apiKey, this.config);

		logger.info("authenticated with frontegg");

		// Get the token and the expiration time
		// Save the token
		this.accessToken = response.getToken();
		// Next refresh is when we have only 20% of the sliding window remaining
		long nextRefresh = (long) ((response.getExpiresIn() * 1000) * 0.8);
		this.accessTokenExpiry = Instant.now().plusSeconds(nextRefresh);
	}

	public void refreshAuthentication()
	{
		this.authenticate(true);
	}

	public void validateAuthentication()
	{
		if (StringHelper.isBlank(this.accessToken) || this.accessTokenExpiry == null || Instant.now()
																							   .isAfter(this.accessTokenExpiry))
		{
			logger.info("authentication token needs refresh - going to refresh it");
			refreshAuthentication();
		}
	}

	public String getAccessToken()
	{
		validateAuthentication();
		return this.accessToken;
	}
}

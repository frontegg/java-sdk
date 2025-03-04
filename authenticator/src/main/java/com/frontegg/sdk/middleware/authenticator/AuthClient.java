package com.frontegg.sdk.middleware.authenticator;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.config.FronteggConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthClient
{
	private static final Logger logger = LoggerFactory.getLogger(AuthClient.class);
	private final ApiClient apiClient;

	public AuthClient(ApiClient apiClient)
	{
		this.apiClient = apiClient;
	}

	public AuthResponse authenticate(String clientId, String apiKey, FronteggConfig config)
	{
		AuthResponse authResponse;
		try
		{
			var request = new AuthRequest(clientId, apiKey);
			var post = this.apiClient.post(config.getUrlConfig().getAuthenticationService(),
			                               AuthResponse.class,
			                               request);
			authResponse = post.getBody();
		}
		catch (Exception ex)
		{
			logger.error("Failed to authenticate with frontegg", ex);
			throw new AuthenticationException("Failed to authenticate with frontegg", ex);
		}
		return authResponse;
	}
}

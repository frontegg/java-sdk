package com.frontegg.sdk.sso;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.common.model.FronteggHttpHeader;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.HttpHelper;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;

import java.util.HashMap;
import java.util.Map;

public class SsoClient
{
	private static final String PRE_LOGIN_PATH = "/resources/sso/v1/prelogin";
	private static final String POST_LOGIN_PATH = "/resources/sso/v1/postlogin";

	private final FronteggAuthenticator authenticator;
	private final ApiClient apiClient;
	private final FronteggConfig fronteggConfig;

	public SsoClient(FronteggAuthenticator authenticator, ApiClient apiClient, FronteggConfig fronteggConfig)
	{
		this.authenticator = authenticator;
		this.apiClient = apiClient;
		this.fronteggConfig = fronteggConfig;
	}

	public String preLogin(String payload)
	{
		String urlPath = this.fronteggConfig.getUrlConfig().getTeamService() + PRE_LOGIN_PATH;
		FronteggHttpResponse<Object> response = this.apiClient.post(urlPath,
																	Object.class,
																	withHeaders(),
																	new SsoRequest(payload));
		validateStatus(urlPath, response);
		FronteggHttpHeader locationHeader = response.getHeaders()
													.stream()
													.filter(fh -> fh.getName().equals("location"))
													.findFirst()
													.orElse(null);
		return locationHeader != null ? locationHeader.getValue() : null;
	}

	public Object postLogin(SamlResponse samlResponse)
	{
		String urlPath = this.fronteggConfig.getUrlConfig().getTeamService() + POST_LOGIN_PATH;
		Map<String, String> explicitValues = new HashMap<>();
		explicitValues.put("SAMLResponse", samlResponse.getSAMLResponse());
		explicitValues.put("RelayState", samlResponse.getRelayState());
		FronteggHttpResponse<Object> response = this.apiClient.post(urlPath,
																	Object.class,
																	withHeaders(),
																	explicitValues);
		validateStatus(urlPath, response);
		return response.getBody();
	}

	private Map<String, String> withHeaders()
	{
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpHelper.FRONTEGG_HEADER_ACCESS_TOKEN, this.authenticator.getAccessToken());
		return headers;
	}

	private void validateStatus(String url, FronteggHttpResponse<?> response)
	{
		if (response.getStatusCode() < 200 || response.getStatusCode() >= 400)
		{
			throw new FronteggSDKException("SSO request to " + url + " fails. invalid response status  = " + response.getStatusCode());
		}
	}
}

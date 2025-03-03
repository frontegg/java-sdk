package com.frontegg.sdk.sso;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.HttpHelper;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SsoClient
{
	private static final String SSO_PATH_PREFIX = "/resources/sso/v1/auth";
	private static final String PRE_LOGIN_PATH = SSO_PATH_PREFIX + "/prelogin";
	private static final String POST_LOGIN_PATH = SSO_PATH_PREFIX + "/postlogin";

	private static final String DEPRECATED_SSO_PATH_PREFIX = "/resources/sso/v1";
	private static final String DEPRECATED_PRE_LOGIN_PATH = DEPRECATED_SSO_PATH_PREFIX + "/prelogin";

	private static final String SAML_PATH = SSO_PATH_PREFIX + "/saml";
	private static final String SAML_CONFIGURATIONS_PATH = SAML_PATH + "/configurations";
	private static final String SAML_VENDOR_CONFIG_PATH = SAML_CONFIGURATIONS_PATH + "/vendor-config";

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
		var urlPath = this.fronteggConfig.getUrlConfig().getTeamService() + PRE_LOGIN_PATH;
		var response = this.apiClient.post(urlPath, Object.class, withHeaders(), new SsoRequest(payload));
		validateStatus(urlPath, response);
		var locationHeader = response.getHeaders()
		                             .stream()
		                             .filter(fh -> fh.getName().equals("location"))
		                             .findFirst()
		                             .orElse(null);
		return locationHeader != null ? locationHeader.getValue() : null;
	}

	public String preLoginWithEmailOrTenantId(String payload)
	{
		var urlPath = this.fronteggConfig.getUrlConfig().getTeamService() + DEPRECATED_PRE_LOGIN_PATH;
		var response = this.apiClient.post(urlPath, Object.class, withHeaders(), new SsoRequestWithPayload(payload));
		validateStatus(urlPath, response);
		var locationHeader = response.getHeaders()
		                             .stream()
		                             .filter(fh -> fh.getName().equals("location"))
		                             .findFirst()
		                             .orElse(null);
		return locationHeader != null ? locationHeader.getValue() : null;
	}


	public Object postLogin(SamlResponse samlResponse)
	{
		var urlPath = this.fronteggConfig.getUrlConfig().getTeamService() + POST_LOGIN_PATH;
		var explicitValues = new HashMap<String, String>();
		explicitValues.put("SAMLResponse", samlResponse.getSAMLResponse());
		explicitValues.put("RelayState", samlResponse.getRelayState());
		var response = this.apiClient.post(urlPath, Object.class, withHeaders(), explicitValues);
		validateStatus(urlPath, response);
		return response.getBody();
	}

	/**
	 * Returns the vendor SSO config
	 */
	public Optional<SamlVendorConfig> getSsoConfiguration()
	{
		var urlPath = this.fronteggConfig.getUrlConfig().getTeamService() + SAML_VENDOR_CONFIG_PATH;
		return this.apiClient.get(urlPath, withHeaders(), SamlVendorConfig.class);
	}

	/**
	 * Returns the tenant SSO configuration
	 */
	public Optional<SamlTenantConfig> getSsoConfiguration(String tenantId)
	{
		var urlPath = this.fronteggConfig.getUrlConfig().getTeamService() + SAML_CONFIGURATIONS_PATH;
		return this.apiClient.get(urlPath, withHeaders(tenantId), SamlTenantConfig.class);
	}

	private Map<String, String> withHeaders()
	{
		var headers = new HashMap<String, String>();
		headers.put(HttpHelper.FRONTEGG_HEADER_ACCESS_TOKEN, this.authenticator.getAccessToken());
		return headers;
	}

	private Map<String, String> withHeaders(String tenantId)
	{
		var headers = withHeaders();
		if (tenantId != null)
		{
			headers.put(HttpHelper.FRONTEGG_HEADER_TENANT_ID, tenantId);
		}
		return headers;
	}

	private void validateStatus(String url, FronteggHttpResponse<?> response)
	{
		if (response.getStatusCode() < 200 || response.getStatusCode() >= 400)
		{
			throw new FronteggSDKException("SSO request to " +
			                               url +
			                               " fails. invalid response status = " +
			                               response.getStatusCode());
		}
	}
}

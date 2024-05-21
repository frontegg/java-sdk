package com.frontegg.sdk.middleware;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.HttpHelper;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.frontegg.sdk.common.util.HttpHelper.*;
import static com.frontegg.sdk.middleware.Constants.FRONTEGG_CONTEXT_KEY;

public class FronteggService
{

	private static final Logger logger = LoggerFactory.getLogger(FronteggService.class);

	private final FronteggConfig config;
	private final ApiClient apiClient;
	private final FronteggAuthenticator authenticator;
	private final FronteggOptions fronteggOptions;

	public FronteggService(
			FronteggConfig config,
			ApiClient apiClient,
			FronteggAuthenticator authenticator,
			FronteggOptions fronteggOptions
	)
	{
		this.config = config;
		this.apiClient = apiClient;
		this.authenticator = authenticator;
		this.fronteggOptions = fronteggOptions;
	}

	public FronteggHttpResponse<Object> doProcess(HttpServletRequest request, HttpServletResponse response)
	{
		var context = (FronteggContext) request.getAttribute(FRONTEGG_CONTEXT_KEY);
		logger.debug("Proxy request ({} -> {})", request.getRequestURI(), this.config.getUrlConfig().getBaseUrl());
		var headers = initHeaders(request, context);

		var requestUrl = request.getRequestURI().substring(this.fronteggOptions.getBasePath().length());
		var url = this.config.getUrlConfig().getBaseUrl() + requestUrl;

		return initiateRequest(url, request, response, headers);
	}

	public void authorizeApplication()
	{
		logger.debug("Refreshing access token");
		this.authenticator.refreshAuthentication();
		logger.debug("Access token refreshed");
	}

	private FronteggHttpResponse<Object> initiateRequest(
			String url, HttpServletRequest request, HttpServletResponse response, Map<String, String> headers
	)
	{
		FronteggHttpResponse<Object> val;
		val = this.apiClient.service(url, request, response, headers, Object.class);

		//Rewrite Cookies
		if (!StringHelper.isBlank(this.fronteggOptions.getCookieDomainRewrite()))
		{
			cookieDomainRewrite(val, request, response);
		}

		return val;
	}

	private void cookieDomainRewrite(
			FronteggHttpResponse<Object> fronteggHttpResponse, HttpServletRequest request, HttpServletResponse response
	)
	{
		var cookies = request.getCookies();
		var host = HttpHelper.getHeader(request, FRONTEGG_HEADER_HOST);
		if (!StringHelper.isBlank(host))
		{
			for (var cookie : cookies)
			{
				if (cookie.getDomain().equals(host))
				{
					cookie.setDomain(this.fronteggOptions.getCookieDomainRewrite());
				}
			}
		}
	}

	private Map<String, String> initHeaders(HttpServletRequest request, FronteggContext context)
	{
		var headers = new HashMap<String, String>();
		headers.put(FRONTEGG_HEADER_ACCESS_TOKEN, this.authenticator.getAccessToken());
		headers.put(
				FRONTEGG_HEADER_TENANT_ID,
				Optional.ofNullable(context).map(FronteggContext::getTenantId).orElse(""));
		headers.put(FRONTEGG_HEADER_USER_ID, Optional.ofNullable(context).map(FronteggContext::getUserId).orElse(""));
		headers.put(FRONTEGG_HEADER_VENDOR_HOST, HttpHelper.getHostnameFromRequest(request));
		return headers;
	}
}

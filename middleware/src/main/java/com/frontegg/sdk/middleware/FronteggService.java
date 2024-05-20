package com.frontegg.sdk.middleware;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.HttpHelper;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authenticator.AuthenticationException;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
		FronteggContext context = (FronteggContext) request.getAttribute(FRONTEGG_CONTEXT_KEY);
		logger.debug("Proxy request ({} -> {})", request.getRequestURI(), this.config.getUrlConfig().getBaseUrl());
		Map<String, String> headers = initHeaders(request, context);

		String requestUrl = request.getRequestURI().substring(this.fronteggOptions.getBasePath().length());
		String url = this.config.getUrlConfig().getBaseUrl() + requestUrl;

		return initiateRequest(url, request, response, headers);
	}

	public void authorizeApplication()
	{
		logger.warn("going to refresh authentication");
		this.authenticator.refreshAuthentication();
		logger.warn("refreshed authentication");
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
		Cookie[] cookies = request.getCookies();
		String host = HttpHelper.getHeader(request, FRONTEGG_HEADER_HOST);
		if (!StringHelper.isBlank(host))
		{
			for (Cookie cookie : cookies)
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
		Map<String, String> headers = new HashMap<>();
		headers.put(FRONTEGG_HEADER_ACCESS_TOKEN, this.authenticator.getAccessToken());
		headers.put(FRONTEGG_HEADER_TENANT_ID, Optional.ofNullable(context).map(FronteggContext::getTenantId).orElse(""));
		headers.put(FRONTEGG_HEADER_USER_ID, Optional.ofNullable(context).map(FronteggContext::getUserId).orElse(""));
		headers.put(FRONTEGG_HEADER_VENDOR_HOST, HttpHelper.getHostnameFromRequest(request));
		return headers;
	}
}

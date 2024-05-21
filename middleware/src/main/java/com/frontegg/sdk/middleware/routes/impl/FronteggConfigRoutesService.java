package com.frontegg.sdk.middleware.routes.impl;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.FronteggOptions;
import com.frontegg.sdk.middleware.routes.IFronteggRouteService;
import com.frontegg.sdk.middleware.routes.model.KeyValPair;
import com.frontegg.sdk.middleware.routes.model.RoutesConfig;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public class FronteggConfigRoutesService implements IFronteggRouteService
{
	private static final String ROUTE_PATH = "/configs/routes";
	private final ApiClient apiClient;
	private final FronteggConfig fronteggConfig;
	private final FronteggOptions fronteggOptions;
	private RoutesConfig routesConfig;

	public FronteggConfigRoutesService(
			ApiClient apiClient, FronteggConfig fronteggConfig, FronteggOptions fronteggOptions
	)
	{
		this.apiClient = apiClient;
		this.fronteggConfig = fronteggConfig;
		this.fronteggOptions = fronteggOptions;
	}

	private void init()
	{
		this.routesConfig = getRoutesConfig();
	}

	public boolean isFronteggPublicRoute(HttpServletRequest request)
	{
		init();

		if (this.routesConfig == null || this.routesConfig.getVendorClientPublicRoutes() == null)
		{
			return false;
		}

		var path = request.getRequestURI().substring(this.fronteggOptions.getBasePath().length()).replaceFirst("/", "");

		for (var vendorClientPublicRoutes : this.routesConfig.getVendorClientPublicRoutes())
		{
			if (!path.equals(vendorClientPublicRoutes.getUrl()))
			{
				continue;
			}

			if (!request.getMethod().toUpperCase().equals(vendorClientPublicRoutes.getMethod()))
			{
				continue;
			}

			if (vendorClientPublicRoutes.getWithQueryParams() != null &&
			    !isValidateQueryParams(request, vendorClientPublicRoutes.getWithQueryParams()))
			{
				continue;
			}

			return true;
		}

		return false;
	}

	private boolean isValidateQueryParams(HttpServletRequest request, List<KeyValPair> withQueryParams)
	{
		var queryMap = request.getParameterMap();

		for (var keyValPair : withQueryParams)
		{
			var key = keyValPair.getKey();
			var val = keyValPair.getValue();

			if (!queryMap.containsKey(key))
			{
				return false;
			}

			if (!StringHelper.stringValueOf(queryMap.get(key)).equals(val))
			{
				return false;
			}
		}
		return true;
	}

	private RoutesConfig getRoutesConfig()
	{
		if (this.routesConfig != null)
		{
			return this.routesConfig;
		}

		return fetchRoutesConfig();
	}

	private RoutesConfig fetchRoutesConfig()
	{
		var url = this.fronteggConfig.getUrlConfig().getBaseUrl() + ROUTE_PATH;
		return this.apiClient.get(url, RoutesConfig.class).orElse(null);
	}
}

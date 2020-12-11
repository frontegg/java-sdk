package com.frontegg.sdk.middleware.routes.impl;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.config.DefaultConfigProvider;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.FronteggOptions;
import com.frontegg.sdk.middleware.routes.model.KeyValPair;
import com.frontegg.sdk.middleware.routes.model.RoutesConfig;
import com.frontegg.sdk.middleware.routes.model.VendorClientPublicRoutes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FronteggConfigRoutesServiceTest
{

	private static final String ROUT_PATH = "/configs/routes";
	private static final RoutesConfig routesConfig = initRoutConfigs();
	private ApiClient apiClient;
	private HttpServletRequest request;
	private FronteggConfigRoutesService fronteggConfigRoutesService;
	private final FronteggConfig config = new DefaultConfigProvider().resolveConfigs();
	private final FronteggOptions options = new FronteggOptions(null, null, true, "", 1, "/frontegg");

	private static RoutesConfig initRoutConfigs()
	{
		RoutesConfig routesConfig = new RoutesConfig();
		List<VendorClientPublicRoutes> vendorClientPublicRouts = new ArrayList<>();
		VendorClientPublicRoutes route = new VendorClientPublicRoutes();
		route.setUrl("identity/resources/auth/v1/user");
		route.setDescription("Authenticate user");
		route.setMethod("POST");
		vendorClientPublicRouts.add(route);

		route = new VendorClientPublicRoutes();
		route.setMethod("GET");
		route.setDescription("Get vendor saml config");
		route.setUrl("metadata");
		List<KeyValPair> withQueryParams = new ArrayList<>();
		KeyValPair keyValPair = new KeyValPair();
		keyValPair.setKey("entityName");
		keyValPair.setValue("saml");
		withQueryParams.add(keyValPair);
		route.setWithQueryParams(withQueryParams);
		vendorClientPublicRouts.add(route);
		routesConfig.setVendorClientPublicRoutes(vendorClientPublicRouts);
		return routesConfig;
	}

	@BeforeEach
	void setUp()
	{
		this.apiClient = mock(ApiClient.class);
		this.request = Mockito.mock(HttpServletRequest.class);

		String url = this.config.getUrlConfig().getBaseUrl() + ROUT_PATH;
		when(this.apiClient.get(url, RoutesConfig.class)).thenReturn(Optional.of(routesConfig));
		this.fronteggConfigRoutesService = new FronteggConfigRoutesService(this.apiClient, this.config, this.options);
	}

	@Test
	public void isFronteggPublicRoute_WithQueryParam_matching()
	{
		Map<String, String[]> queryMap = new HashMap<>();
		queryMap.put("entityName", new String[]{ "saml" });
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

		when(request.getRequestURI()).thenReturn("/frontegg/metadata");
		when(request.getMethod()).thenReturn("GET");
		when(request.getParameterMap()).thenReturn(queryMap);
		assertTrue(this.fronteggConfigRoutesService.isFronteggPublicRoute(request));
	}

	@Test
	public void isFronteggPublicRoute_WithQueryParam_notMatchingQueryParam()
	{
		Map queryMap = new HashMap<>();
		queryMap.put("entityName", new String[]{ "saml2" });
		when(this.request.getRequestURI()).thenReturn("/frontegg/metadata");
		when(this.request.getMethod()).thenReturn("GET");
		when(this.request.getParameterMap()).thenReturn(queryMap);
		assertFalse(this.fronteggConfigRoutesService.isFronteggPublicRoute(this.request));
	}

	@Test
	public void isFronteggPublicRoute_WithQueryParam_notMatchingMethod()
	{
		Map queryMap = new HashMap<>();
		queryMap.put("entityName", new String[]{ "saml" });
		when(this.request.getRequestURI()).thenReturn("/frontegg/metadata");
		when(this.request.getMethod()).thenReturn("POST");
		when(this.request.getParameterMap()).thenReturn(queryMap);
		assertFalse(this.fronteggConfigRoutesService.isFronteggPublicRoute(this.request));
	}

	@Test
	public void isFronteggPublicRoute_withoutQueryParam_matching()
	{
		when(this.request.getRequestURI()).thenReturn("/frontegg/identity/resources/auth/v1/user");
		when(this.request.getMethod()).thenReturn("POST");
		assertTrue(this.fronteggConfigRoutesService.isFronteggPublicRoute(this.request));
	}

	@Test
	public void isFronteggPublicRoute_notMatchingUrl()
	{
		when(this.request.getRequestURI()).thenReturn("/frontegg/identity/resources/auth/");
		when(this.request.getMethod()).thenReturn("POST");
		assertFalse(this.fronteggConfigRoutesService.isFronteggPublicRoute(this.request));
	}
}
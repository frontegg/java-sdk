package com.frontegg.sdk.middleware.routes.impl;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.config.DefaultConfigProvider;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.FronteggOptions;
import com.frontegg.sdk.middleware.routes.model.KeyValPair;
import com.frontegg.sdk.middleware.routes.model.RoutesConfig;
import com.frontegg.sdk.middleware.routes.model.VendorClientPublicRoutes;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class FronteggConfigRoutesServiceTest
{
	private static final String ROUTE_PATH = "/configs/routes";
	private static final RoutesConfig ROUTES_CONFIG = initRoutConfigs();
	private static final FronteggConfig config = new DefaultConfigProvider().resolveConfigs();
	private static final FronteggOptions options = new FronteggOptions(null, null, true, "", 1, "/frontegg");
	private static final String URL = config.getUrlConfig().getBaseUrl() + ROUTE_PATH;

	private static RoutesConfig initRoutConfigs()
	{
		var routesConfig = new RoutesConfig();
		var vendorClientPublicRouts = new ArrayList<VendorClientPublicRoutes>();
		var route = new VendorClientPublicRoutes();
		route.setUrl("identity/resources/auth/v1/user");
		route.setDescription("Authenticate user");
		route.setMethod("POST");
		vendorClientPublicRouts.add(route);

		route = new VendorClientPublicRoutes();
		route.setMethod("GET");
		route.setDescription("Get vendor saml config");
		route.setUrl("metadata");
		var withQueryParams = new ArrayList<KeyValPair>();
		var keyValPair = new KeyValPair();
		keyValPair.setKey("entityName");
		keyValPair.setValue("saml");
		withQueryParams.add(keyValPair);
		route.setWithQueryParams(withQueryParams);
		vendorClientPublicRouts.add(route);
		routesConfig.setVendorClientPublicRoutes(vendorClientPublicRouts);
		return routesConfig;
	}

	public void setup(ApiClient mockApiClient)
	{
		when(mockApiClient.get(URL, RoutesConfig.class)).thenReturn(Optional.of(ROUTES_CONFIG));
	}

	@Test
	public void isFronteggPublicRoute_WithQueryParam_matching(
			@Mock ApiClient mockApiClient, @Mock HttpServletRequest mockRequest
	)
	{
		setup(mockApiClient);
		var cut = new FronteggConfigRoutesService(mockApiClient, config, options);
		var queryMap = new HashMap<String, String[]>();
		queryMap.put("entityName", new String[]{ "saml" });

		when(mockRequest.getRequestURI()).thenReturn("/frontegg/metadata");
		when(mockRequest.getMethod()).thenReturn("GET");
		when(mockRequest.getParameterMap()).thenReturn(queryMap);
		assertTrue(cut.isFronteggPublicRoute(mockRequest));
	}

	@Test
	public void isFronteggPublicRoute_WithQueryParam_notMatchingQueryParam(
			@Mock ApiClient mockApiClient, @Mock HttpServletRequest mockRequest
	)
	{
		setup(mockApiClient);
		var cut = new FronteggConfigRoutesService(mockApiClient, config, options);
		var queryMap = new HashMap<String, String[]>();
		queryMap.put("entityName", new String[]{ "saml2" });
		when(mockRequest.getRequestURI()).thenReturn("/frontegg/metadata");
		when(mockRequest.getMethod()).thenReturn("GET");
		when(mockRequest.getParameterMap()).thenReturn(queryMap);
		assertFalse(cut.isFronteggPublicRoute(mockRequest));
	}

	@Test
	public void isFronteggPublicRoute_WithQueryParam_notMatchingMethod(
			@Mock ApiClient mockApiClient, @Mock HttpServletRequest mockRequest
	)
	{
		setup(mockApiClient);
		var cut = new FronteggConfigRoutesService(mockApiClient, config, options);
		when(mockRequest.getRequestURI()).thenReturn("/frontegg/metadata");
		when(mockRequest.getMethod()).thenReturn("POST");
		assertFalse(cut.isFronteggPublicRoute(mockRequest));
	}

	@Test
	public void isFronteggPublicRoute_withoutQueryParam_matching(
			@Mock ApiClient mockApiClient, @Mock HttpServletRequest mockRequest
	)
	{
		setup(mockApiClient);
		var cut = new FronteggConfigRoutesService(mockApiClient, config, options);
		when(mockRequest.getRequestURI()).thenReturn("/frontegg/identity/resources/auth/v1/user");
		when(mockRequest.getMethod()).thenReturn("POST");
		assertTrue(cut.isFronteggPublicRoute(mockRequest));
	}

	@Test
	public void isFronteggPublicRoute_notMatchingUrl(
			@Mock ApiClient mockApiClient, @Mock HttpServletRequest mockRequest
	)
	{
		setup(mockApiClient);
		var cut = new FronteggConfigRoutesService(mockApiClient, config, options);
		when(mockRequest.getRequestURI()).thenReturn("/frontegg/identity/resources/auth/");
		assertFalse(cut.isFronteggPublicRoute(mockRequest));
	}
}
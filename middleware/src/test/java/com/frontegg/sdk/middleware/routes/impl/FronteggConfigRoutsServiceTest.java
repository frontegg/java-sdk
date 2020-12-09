package com.frontegg.sdk.middleware.routes.impl;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.config.DefaultConfigProvider;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.FronteggContextHolder;
import com.frontegg.sdk.middleware.routes.model.KeyValPair;
import com.frontegg.sdk.middleware.routes.model.RoutesConfig;
import com.frontegg.sdk.middleware.routes.model.VendorClientPublicRouts;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FronteggConfigRoutsServiceTest {

    private ApiClient apiClient;
    private HttpServletRequest request;

    private FronteggConfigRoutsService fronteggConfigRoutsService;

    private static final String ROUT_PATH = "/configs/routes";
    private static RoutesConfig routesConfig = initRoutConfigs();
    private FronteggConfig config = new DefaultConfigProvider().resolveConfigs();

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        request = Mockito.mock(HttpServletRequest.class);

        FronteggContext context = FronteggContextHolder.createEmptyContext();
        context.setFronteggBasePath("/frontegg");
        FronteggContextHolder.setContext(context);


        String url = config.getUrlConfig().getBaseUrl() + ROUT_PATH;
        when(apiClient.get(url, RoutesConfig.class)).thenReturn(Optional.of(routesConfig));
        fronteggConfigRoutsService = new FronteggConfigRoutsService(apiClient, config);
    }

    @Test
    public void isFronteggPublicRoute_WithQueryParam_matching() {
        Map<String, String[]> queryMap = new HashMap<>();
        queryMap.put("entityName", new String[]{"saml"});
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        when(request.getRequestURI()).thenReturn("/frontegg/metadata");
        when(request.getMethod()).thenReturn("GET");
        when(request.getParameterMap()).thenReturn(queryMap);
        assertTrue(fronteggConfigRoutsService.isFronteggPublicRoute(request));
    }

    @Test
    public void isFronteggPublicRoute_WithQueryParam_notMatchingQueryParam() {
        Map queryMap = new HashMap<>();
        queryMap.put("entityName", new String[]{"saml2"});
        when(request.getRequestURI()).thenReturn("/frontegg/metadata");
        when(request.getMethod()).thenReturn("GET");
        when(request.getParameterMap()).thenReturn(queryMap);
        assertFalse(fronteggConfigRoutsService.isFronteggPublicRoute(request));
    }

    @Test
    public void isFronteggPublicRoute_WithQueryParam_notMatchingMethod() {
        Map queryMap = new HashMap<>();
        queryMap.put("entityName", new String[]{"saml"});
        when(request.getRequestURI()).thenReturn("/frontegg/metadata");
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameterMap()).thenReturn(queryMap);
        assertFalse(fronteggConfigRoutsService.isFronteggPublicRoute(request));
    }

    @Test
    public void isFronteggPublicRoute_withoutQueryParam_matching() {
        when(request.getRequestURI()).thenReturn("/frontegg/identity/resources/auth/v1/user");
        when(request.getMethod()).thenReturn("POST");
        assertTrue(fronteggConfigRoutsService.isFronteggPublicRoute(request));
    }

    @Test
    public void isFronteggPublicRoute_notMatchingUrl() {
        when(request.getRequestURI()).thenReturn("/frontegg/identity/resources/auth/");
        when(request.getMethod()).thenReturn("POST");
        assertFalse(fronteggConfigRoutsService.isFronteggPublicRoute(request));
    }

    private static RoutesConfig initRoutConfigs() {
        RoutesConfig routesConfig = new RoutesConfig();
        List<VendorClientPublicRouts> vendorClientPublicRouts = new ArrayList<>();
        VendorClientPublicRouts rout = new VendorClientPublicRouts();
        rout.setUrl("identity/resources/auth/v1/user");
        rout.setDescription("Authenticate user");
        rout.setMethod("POST");
        vendorClientPublicRouts.add(rout);

        rout = new VendorClientPublicRouts();
        rout.setMethod("GET");
        rout.setDescription("Get vendor saml config");
        rout.setUrl("metadata");
        List<KeyValPair> withQueryParams = new ArrayList<>();
        KeyValPair keyValPair = new KeyValPair();
        keyValPair.setKey("entityName");
        keyValPair.setValue("saml");
        withQueryParams.add(keyValPair);
        rout.setWithQueryParams(withQueryParams);
        vendorClientPublicRouts.add(rout);
        routesConfig.setVendorClientPublicRoutes(vendorClientPublicRouts);
        return routesConfig;
    }
}
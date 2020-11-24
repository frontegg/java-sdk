package com.frontegg.sdk.middleware.routes.impl;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.common.util.HttpUtil;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.context.FronteggContextHolder;
import com.frontegg.sdk.middleware.routes.IFronteggRouteService;
import com.frontegg.sdk.middleware.routes.model.KeyValPair;
import com.frontegg.sdk.middleware.routes.model.RoutesConfig;
import com.frontegg.sdk.middleware.routes.model.VendorClientPublicRouts;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public class FronteggConfigRoutsService implements IFronteggRouteService {

    private IApiClient apiClient;
    private FronteggConfig fronteggConfig;
    private RoutesConfig routesConfig;

    private static final String ROUT_PATH = "/configs/routes";

    public FronteggConfigRoutsService(IApiClient apiClient, FronteggConfig fronteggConfig) {
        this.apiClient = apiClient;
        this.fronteggConfig = fronteggConfig;
    }

    private void init() {
        routesConfig = getRoutesConfig();
    }

    public boolean isFronteggPublicRoute(HttpServletRequest request) {
        init();

        if (routesConfig == null || routesConfig.getVendorClientPublicRoutes() == null) return false;

        String path = HttpUtil.getRequestUrl(
                request.getRequestURI(), FronteggContextHolder.getContext().getFronteggBasePath())
                .replaceFirst("/", ""
        );

        for (VendorClientPublicRouts vendorClientPublicRouts : routesConfig.getVendorClientPublicRoutes()) {
            if (!path.equals(vendorClientPublicRouts.getUrl())) {
                continue;
            }

            if (!request.getMethod().toUpperCase().equals(vendorClientPublicRouts.getMethod())) {
                continue;
            }

            if (vendorClientPublicRouts.getWithQueryParams() != null && !isValidateQueryParams(request, vendorClientPublicRouts.getWithQueryParams())) {
                continue;
            }

            return true;
        }

        return false;
    }

    private boolean isValidateQueryParams(HttpServletRequest request, List<KeyValPair> withQueryParams) {
        Map<String, String[]> queryMap = request.getParameterMap();
        boolean hasAllQueryParams = true;

        for (KeyValPair keyValPair : withQueryParams) {
            String key = keyValPair.getKey();
            String val = keyValPair.getValue();
            if (!queryMap.keySet().contains(key)) hasAllQueryParams =  false;
            if (!StringHelper.stringValueOf(queryMap.get(key)).equals(val)) hasAllQueryParams = false;


            if (!hasAllQueryParams) return false;
        }
        return hasAllQueryParams;
    }

    private RoutesConfig getRoutesConfig() {
        if (routesConfig != null) return routesConfig;

        return fetchRoutesConfig();
    }

    private RoutesConfig fetchRoutesConfig() {
        String url = fronteggConfig.getUrlConfig().getBaseUrl() +ROUT_PATH;
        return apiClient.get(url, RoutesConfig.class).orElse(null);
    }
}

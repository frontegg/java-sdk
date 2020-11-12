package com.frontegg.sdk.middleware.spring;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.spring.routes.RoutesConfig;
import com.frontegg.sdk.middleware.spring.routes.VendorClientPublicRouts;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public class FronteggConfigRoutsService {

    private IApiClient apiClient;
    private FronteggConfig fronteggConfig;

    public FronteggConfigRoutsService(IApiClient apiClient, FronteggConfig fronteggConfig) {
        this.apiClient = apiClient;
        this.fronteggConfig = fronteggConfig;
    }

    public boolean isFronteggPublicRoute(HttpServletRequest request) {
        RoutesConfig routesConfig = fetchRoutesConfig();
        if (routesConfig == null || routesConfig.getVendorClientPublicRoutes() == null) return false;
        String path = request.getRequestURI().replace("/", "");

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

    private boolean isValidateQueryParams(HttpServletRequest request, List<Map<String, String>> withQueryParams) {

        Map<String, String> queryMap = request.getParameterMap();
        boolean hasAllQueryParams = false;
        for (Map<String, String> queryParam : withQueryParams) {
            for (String key : queryParam.keySet()) {
                if (!queryMap.keySet().contains(key)) hasAllQueryParams = false;

                //if (queryMap.get(key).equals(queryParam.get(key))) hasAllQueryParams = false;

                if (!hasAllQueryParams) return false;
            }
        }
         return hasAllQueryParams;
    }

    private RoutesConfig fetchRoutesConfig() {
        String url = fronteggConfig.getUrlConfig().getBaseUrl() + "/configs/routes";
        return apiClient.get(url, RoutesConfig.class).orElse(null);
    }

}

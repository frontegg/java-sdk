package com.frontegg.sdk.middleware.spring.service.impl;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.common.util.HttpUtil;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.spring.routes.KeyValPair;
import com.frontegg.sdk.middleware.spring.routes.RoutesConfig;
import com.frontegg.sdk.middleware.spring.routes.VendorClientPublicRouts;
import com.frontegg.sdk.middleware.spring.service.IFronteggRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
public class FronteggConfigRoutsService implements IFronteggRouteService {

    @Autowired
    private IApiClient apiClient;
    @Autowired
    private FronteggConfig fronteggConfig;

    public boolean isFronteggPublicRoute(HttpServletRequest request) {
        RoutesConfig routesConfig = fetchRoutesConfig();
        if (routesConfig == null || routesConfig.getVendorClientPublicRoutes() == null) return false;
        String path = HttpUtil.getRequestUrl(request.getRequestURI(), "/frontegg").replaceFirst("/", "");

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

        //TODO
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

    private RoutesConfig fetchRoutesConfig() {
        String url = fronteggConfig.getUrlConfig().getBaseUrl() + "/configs/routes";
        return apiClient.get(url, RoutesConfig.class).orElse(null);
    }

}

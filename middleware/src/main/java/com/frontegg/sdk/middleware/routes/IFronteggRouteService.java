package com.frontegg.sdk.middleware.routes;

import jakarta.servlet.http.HttpServletRequest;

public interface IFronteggRouteService {

    /**
     * Checks whether the requested url is public rout defined in frontegg api.
     */
    boolean isFronteggPublicRoute(HttpServletRequest request);
}

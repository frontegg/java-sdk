package com.frontegg.sdk.middleware.routes;

import javax.servlet.http.HttpServletRequest;

public interface IFronteggRouteService {

    boolean isFronteggPublicRoute(HttpServletRequest request);
}

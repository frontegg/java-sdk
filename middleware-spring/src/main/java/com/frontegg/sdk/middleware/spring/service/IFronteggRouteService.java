package com.frontegg.sdk.middleware.spring.service;

import javax.servlet.http.HttpServletRequest;

public interface IFronteggRouteService {

    boolean isFronteggPublicRoute(HttpServletRequest request);
}

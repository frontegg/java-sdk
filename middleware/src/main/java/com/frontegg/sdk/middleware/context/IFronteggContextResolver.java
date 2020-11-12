package com.frontegg.sdk.middleware.context;


import javax.servlet.http.HttpServletRequest;

public interface IFronteggContextResolver {

    RequestContext resolveContext(HttpServletRequest request);
}

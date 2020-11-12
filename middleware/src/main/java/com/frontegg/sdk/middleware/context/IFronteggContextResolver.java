package com.frontegg.sdk.middleware.context;

import javax.servlet.http.HttpServletRequest;

public interface IFronteggContextResolver {

    FronteggContext resolveContext(HttpServletRequest request);
}

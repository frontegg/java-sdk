package com.frontegg.sdk.middleware.context;

import javax.servlet.http.HttpServletRequest;

public interface FronteggContextResolver {

    void resolveContext(HttpServletRequest request);
}

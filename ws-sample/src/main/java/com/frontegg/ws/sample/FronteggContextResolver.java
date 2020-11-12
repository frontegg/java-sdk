package com.frontegg.ws.sample;

import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.IFronteggContextResolver;

import javax.servlet.http.HttpServletRequest;

public class FronteggContextResolver implements IFronteggContextResolver {

    @Override
    public FronteggContext resolveContext(HttpServletRequest request) {
        return new FronteggContext();
    }
}

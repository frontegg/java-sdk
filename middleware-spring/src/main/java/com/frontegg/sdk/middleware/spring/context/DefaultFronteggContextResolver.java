package com.frontegg.sdk.middleware.spring.context;

import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.IFronteggContextResolver;
import com.frontegg.sdk.middleware.context.RequestContext;

import javax.servlet.http.HttpServletRequest;

public class DefaultFronteggContextResolver implements IFronteggContextResolver {

    @Override
    public RequestContext resolveContext(HttpServletRequest request) {
        String requestUrl = request.getRequestURI();
        requestUrl = requestUrl.substring("/frontegg".length());

        RequestContext requestContext = new RequestContext();
        requestContext.setRequestPath(requestUrl);
        requestContext.setFronteggContext(new FronteggContext());
        return requestContext;
    }
}

package com.frontegg.sdk.middleware.spring.context;

import com.frontegg.sdk.common.util.HttpUtil;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.IFronteggContextResolver;
import com.frontegg.sdk.middleware.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class DefaultFronteggContextResolver implements IFronteggContextResolver {

    //TODO make it configurable
    public static final String CONTEXT_MAIN_PATH = "/frontegg";

    @Autowired
    private IPermissionResolver permissionResolver;

    @Override
    public RequestContext resolveContext(HttpServletRequest request) {
        String requestUrl = request.getRequestURI();
        requestUrl = HttpUtil.getRequestUrl(requestUrl, CONTEXT_MAIN_PATH);

        RequestContext requestContext = new RequestContext();
        requestContext.setRequestPath(requestUrl);


        FronteggContext fronteggContext = new FronteggContext();
        fronteggContext.setPermissions(permissionResolver.resolveAppPermissions());
        fronteggContext.setUserId("");
        fronteggContext.setTenantId("");
        requestContext.setFronteggContext(fronteggContext);

        return requestContext;
    }
}

package com.frontegg.sdk.middleware.spring.context;

import com.frontegg.sdk.common.util.HttpUtil;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.IFronteggContextResolver;
import com.frontegg.sdk.middleware.context.RequestContext;
import com.frontegg.sdk.middleware.model.Permission;
import com.frontegg.sdk.middleware.permission.FrontEggPermissionEnum;
import com.frontegg.sdk.middleware.permission.PermissionActionEnum;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

public class DefaultFronteggContextResolver implements IFronteggContextResolver {

    //TODO make it configurable
    public static final String CONTEXT_MAIN_PATH = "/frontegg";

    @Override
    public RequestContext resolveContext(HttpServletRequest request) {
        String requestUrl = request.getRequestURI();
        requestUrl = HttpUtil.getRequestUrl(requestUrl, CONTEXT_MAIN_PATH);

        RequestContext requestContext = new RequestContext();
        requestContext.setRequestPath(requestUrl);


        FronteggContext fronteggContext = new FronteggContext();
        fronteggContext.setPermissions(getAppPermissions());
        fronteggContext.setUserId("");
        fronteggContext.setTenantId("");
        requestContext.setFronteggContext(fronteggContext);

        return requestContext;
    }

    public List<Permission> getAppPermissions() {
        return Arrays.asList(
                FrontEggPermissionEnum.AUDITS.with(
                        PermissionActionEnum.READ,
                        PermissionActionEnum.STATS
                ),

                FrontEggPermissionEnum.TENANTS.with(
                        PermissionActionEnum.ALL
                )
        );
    }
}

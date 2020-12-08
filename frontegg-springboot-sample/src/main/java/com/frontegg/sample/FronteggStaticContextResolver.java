package com.frontegg.sample;

import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.FronteggContextResolver;

public class FronteggStaticContextResolver implements FronteggContextResolver {

    private final String tenantId;
    private final String userId;

    FronteggStaticContextResolver(String tenantId, String userId) {
        this.tenantId = tenantId;
        this.userId = userId;
    }

    @Override
    public FronteggContext resolveContext() {
        FronteggContext fronteggContext = new FronteggContext();
        fronteggContext.setTenantId(tenantId);
        fronteggContext.setUserId(userId);
        return fronteggContext;
    }
}

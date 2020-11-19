package com.frontegg.sdk.middleware.spring.context;

import com.frontegg.sdk.middleware.context.FronteggContext;

public class FronteggContextHolder {
    private static final ThreadLocal<FronteggContext> contextHolder = new ThreadLocal<>();

    public static FronteggContext createEmptyContext() {
        return new FronteggContext();
    }

    public static void setContext(FronteggContext fronteggContext) {
        contextHolder.set(fronteggContext);
    }

    public static void clearContext() {
        contextHolder.remove();
    }

    public static FronteggContext getContext() {
        return contextHolder.get();
    }
}

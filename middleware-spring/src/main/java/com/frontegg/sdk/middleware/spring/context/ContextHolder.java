package com.frontegg.sdk.middleware.spring.context;

import com.frontegg.sdk.middleware.authenticator.Authentication;
import com.frontegg.sdk.middleware.context.RequestContext;

public class ContextHolder {

    private static final ThreadLocal<Authentication> authContextHolder = new ThreadLocal<>();
    private static final ThreadLocal<RequestContext> requestContextHolder = new ThreadLocal<>();

    public static Authentication getAuthentication() {
        return authContextHolder.get();
    }
    public static void setAuthenctication(Authentication authenctication) {
        authContextHolder.set(authenctication);
    }
    public static void removeAuthenticationContext() {
        authContextHolder.remove();
    }

    public static RequestContext getRequestContext() {
        return requestContextHolder.get();
    }
    public static void removeRequestContext() {
        requestContextHolder.remove();
    }
    public static void setRequestContext(RequestContext context) {
        requestContextHolder.set(context);
    }
}

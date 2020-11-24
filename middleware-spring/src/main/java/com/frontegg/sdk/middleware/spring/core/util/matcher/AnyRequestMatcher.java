package com.frontegg.sdk.middleware.spring.core.util.matcher;

import javax.servlet.http.HttpServletRequest;


/**
 * Matches any supplied request.
 */
public final class AnyRequestMatcher implements RequestMatcher {
    public static final RequestMatcher INSTANCE = new AnyRequestMatcher();

    public boolean matches(HttpServletRequest request) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean equals(Object obj) {
        return obj instanceof AnyRequestMatcher
                || obj instanceof AnyRequestMatcher;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "any request";
    }

    private AnyRequestMatcher() {
    }
}

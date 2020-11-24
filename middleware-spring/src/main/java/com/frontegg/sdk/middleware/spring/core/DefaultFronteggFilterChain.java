package com.frontegg.sdk.middleware.spring.core;

import com.frontegg.sdk.middleware.spring.core.util.matcher.RequestMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DefaultFronteggFilterChain implements FronteggFilterChain {
    private static final Logger logger = LoggerFactory.getLogger(DefaultFronteggFilterChain.class);
    private final RequestMatcher requestMatcher;
    private final List<Filter> filters;

    public DefaultFronteggFilterChain(RequestMatcher requestMatcher, Filter... filters) {
        this(requestMatcher, Arrays.asList(filters));
    }

    public DefaultFronteggFilterChain(RequestMatcher requestMatcher, List<Filter> filters) {
        logger.info("Creating filter chain: " + requestMatcher + ", " + filters);
        this.requestMatcher = requestMatcher;
        this.filters = new ArrayList<>(filters);
    }

    public RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public boolean matches(HttpServletRequest request) {
        return requestMatcher.matches(request);
    }

    @Override
    public String toString() {
        return "[ " + requestMatcher + ", " + filters + "]";
    }
}

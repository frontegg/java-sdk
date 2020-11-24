package com.frontegg.sdk.middleware.spring.core;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * Defines a filter chain which is capable of being matched against an
 * {@code HttpServletRequest}. in order to decide whether it applies to that request.
 * <p>
 * Used to configure a {@code FilterChainProxy}.
 *
 */
public interface FronteggFilterChain {

    boolean matches(HttpServletRequest request);

    List<Filter> getFilters();
}

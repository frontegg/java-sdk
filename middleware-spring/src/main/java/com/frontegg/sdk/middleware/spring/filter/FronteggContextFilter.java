package com.frontegg.sdk.middleware.spring.filter;

import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.FronteggContextHolder;
import com.frontegg.sdk.middleware.spring.core.context.FronteggContextRepository;
import com.frontegg.sdk.middleware.spring.core.context.FronteggHttpRequestResponseHolder;
import com.frontegg.sdk.middleware.spring.core.context.HttpSessionFronteggContextRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FronteggContextFilter extends FronteggBaseFilter {

    static final String FILTER_APPLIED = "__frontegg_feggf_applied";

    private FronteggContextRepository repo;

    public FronteggContextFilter() {
        this(new HttpSessionFronteggContextRepository());
    }

    public FronteggContextFilter(FronteggContextRepository repo) {
        this.repo = repo;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (requestMatcher.matches(request)) {

            if (request.getAttribute(FILTER_APPLIED) != null) {
                // ensure that filter is only applied once per request
                chain.doFilter(request, response);
                return;
            }

            request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

            FronteggHttpRequestResponseHolder holder = new FronteggHttpRequestResponseHolder(request, response);
            FronteggContext contextBeforeChainExecution = repo.loadContext(holder);

            try {
                FronteggContextHolder.setContext(contextBeforeChainExecution);

                chain.doFilter(holder.getRequest(), holder.getResponse());

            } finally {
                FronteggContext contextAfterChainExecution = FronteggContextHolder.getContext();
                FronteggContextHolder.clearContext();
                repo.saveContext(contextAfterChainExecution, holder.getRequest(), holder.getResponse());
                request.removeAttribute(FILTER_APPLIED);
            }

            return;
        }

        chain.doFilter(servletRequest, servletResponse);

    }
}

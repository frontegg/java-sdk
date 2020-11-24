package com.frontegg.sdk.middleware.spring.core;


import com.frontegg.sdk.middleware.context.FronteggContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FilterChainProxy extends GenericFilterBean {
    private static final Log logger = LogFactory.getLog(FilterChainProxy.class);
    private final static String FILTER_APPLIED = FilterChainProxy.class.getName().concat(".APPLIED");

    private List<FronteggFilterChain> filterChains;
    private FilterChainValidator filterChainValidator = new NullFilterChainValidator();

    public FilterChainProxy() {
    }

    public FilterChainProxy(FronteggFilterChain chain) {
        this(Arrays.asList(chain));
    }

    public FilterChainProxy(List<FronteggFilterChain> filterChains) {
        this.filterChains = filterChains;
    }

    @Override
    public void afterPropertiesSet() {
        filterChainValidator.validate(this);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        boolean clearContext = request.getAttribute(FILTER_APPLIED) == null;
        if (clearContext) {
            try {
                request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
                doFilterInternal(request, response, chain);
            }
            finally {
                FronteggContextHolder.clearContext();
                request.removeAttribute(FILTER_APPLIED);
            }
        }
        else {
            doFilterInternal(request, response, chain);
        }
    }

    private void doFilterInternal(ServletRequest request, ServletResponse response,
                                  FilterChain chain) throws IOException, ServletException {

        List<Filter> filters = getFilters((HttpServletRequest) request);

        if (filters == null || filters.size() == 0) {
            chain.doFilter(request, response);

            return;
        }

        VirtualFilterChain vfc = new VirtualFilterChain(chain, filters);
        vfc.doFilter(request, response);
    }

    private List<Filter> getFilters(HttpServletRequest request) {
        for (FronteggFilterChain chain : filterChains) {
            if (chain.matches(request)) {
                return chain.getFilters();
            }
        }

        return null;
    }

    public List<FronteggFilterChain> getFilterChains() {
        return Collections.unmodifiableList(filterChains);
    }

    public void setFilterChainValidator(FilterChainValidator filterChainValidator) {
        this.filterChainValidator = filterChainValidator;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FilterChainProxy[");
        sb.append("Filter Chains: ");
        sb.append(filterChains);
        sb.append("]");

        return sb.toString();
    }

    private static class VirtualFilterChain implements FilterChain {
        private final FilterChain originalChain;
        private final List<Filter> additionalFilters;
        private final int size;
        private int currentPosition = 0;

        private VirtualFilterChain(FilterChain chain, List<Filter> additionalFilters) {
            this.originalChain = chain;
            this.additionalFilters = additionalFilters;
            this.size = additionalFilters.size();
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response)
                throws IOException, ServletException {
            if (currentPosition == size) {
                originalChain.doFilter(request, response);
            }
            else {
                currentPosition++;

                Filter nextFilter = additionalFilters.get(currentPosition - 1);

                nextFilter.doFilter(request, response, this);
            }
        }
    }

    public interface FilterChainValidator {
        void validate(FilterChainProxy filterChainProxy);
    }

    private static class NullFilterChainValidator implements FilterChainValidator {
        @Override
        public void validate(FilterChainProxy filterChainProxy) {
        }
    }

}

package com.frontegg.sdk.middleware.spring.core.configurers;

import com.frontegg.sdk.middleware.spring.core.DefaultFronteggFilterChain;
import com.frontegg.sdk.middleware.spring.core.FronteggAppConfigurerAdapter;
import com.frontegg.sdk.middleware.spring.core.builders.FronteggAppBuilder;
import com.frontegg.sdk.middleware.spring.core.util.matcher.AntPathRequestMatcher;
import com.frontegg.sdk.middleware.spring.core.util.matcher.OrRequestMatcher;
import com.frontegg.sdk.middleware.spring.core.util.matcher.RequestMatcher;
import com.frontegg.sdk.middleware.spring.filter.FronteggBaseFilter;


public class FronteggFilterConfigurer<H extends FronteggAppBuilder<H>> extends FronteggAppConfigurerAdapter<DefaultFronteggFilterChain, H> {
    static final String DEFAULT_FRONTEGG_BASE_URL = "/frontegg";
    private String path = DEFAULT_FRONTEGG_BASE_URL;

    private FronteggBaseFilter[] filters;

    private RequestMatcher requestMatcher;

    public FronteggFilterConfigurer<H> filter(String path, FronteggBaseFilter ...filter) {
        this.path = path;
        this.filters = filter;
        return this;
    }

    @Override
    public void init(H frontegg) {
        for (FronteggBaseFilter fronteggBaseFilter : filters) {
            fronteggBaseFilter.setRequestMatcher(getFronteggFilterRequestMatcher(frontegg));
            postProcess(fronteggBaseFilter);
        }
    }

    @Override
    public void configure(H frontegg) throws Exception {
        for (FronteggBaseFilter fronteggBaseFilter : filters) {
            fronteggBaseFilter.setBasePath(path);
            frontegg.addFilter(fronteggBaseFilter);
        }
    }

    @SuppressWarnings("unchecked")
    private RequestMatcher getFronteggFilterRequestMatcher(H frontegg) {
        if (requestMatcher != null) {
            return requestMatcher;
        }

        this.requestMatcher = new OrRequestMatcher(
                new AntPathRequestMatcher(this.path + "/**", "GET"),
                new AntPathRequestMatcher(this.path+ "/**", "POST"),
                new AntPathRequestMatcher(this.path+ "/**", "PUT"),
                new AntPathRequestMatcher(this.path+ "/**", "DELETE"),
                new AntPathRequestMatcher(this.path+ "/**", "OPTIONS")
        );

        return this.requestMatcher;
    }
}

package com.frontegg.sdk.middleware.spring.core.builders;

import com.frontegg.sdk.middleware.spring.core.DefaultFronteggFilterChain;
import com.frontegg.sdk.middleware.spring.core.FronteggAppConfigurerAdapter;
import com.frontegg.sdk.middleware.spring.core.ObjectPostProcessor;
import com.frontegg.sdk.middleware.spring.core.configurers.CorsConfigurer;
import com.frontegg.sdk.middleware.spring.core.configurers.FronteggFilterConfigurer;
import com.frontegg.sdk.middleware.spring.core.util.matcher.AnyRequestMatcher;
import com.frontegg.sdk.middleware.spring.core.util.matcher.RequestMatcher;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Frontegg extends
        AbstractConfiguredFronteggBuilder<DefaultFronteggFilterChain, Frontegg> implements
        FronteggBuilder<DefaultFronteggFilterChain>,
        FronteggAppBuilder<Frontegg> {


    private List<Filter> filters = new ArrayList<>();
    private RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;

    public Frontegg(ObjectPostProcessor<Object> objectPostProcessor, Map<Class<?>, Object> sharedObjects) {
        super(objectPostProcessor);
        for (Map.Entry<Class<?>, Object> entry : sharedObjects
                .entrySet()) {
            setSharedObject((Class<Object>) entry.getKey(), entry.getValue());
        }
    }

    public Frontegg appConfiguration(String clientID, String apiKey) {
        return this;
    }

    public Frontegg maxRetries(int maxRetries) {
        return this;
    }

    public Frontegg cookieDomainRewrite(String cookieOverride) {
        return this;
    }

    public Frontegg basePath(String basePath) {

        return this;
    }

    public FronteggFilterConfigurer<Frontegg> fronteggFilters() throws Exception {
        return getOrApply(new FronteggFilterConfigurer<>());
    }

    public CorsConfigurer<Frontegg> cors() throws Exception {
        return getOrApply(new CorsConfigurer<>());
    }

    @Override
    protected DefaultFronteggFilterChain performBuild() {
        return new DefaultFronteggFilterChain(requestMatcher, filters);
    }

    public Frontegg addFilter(Filter filter) {
        this.filters.add(filter);
        return this;
    }

    private <C extends FronteggAppConfigurerAdapter<DefaultFronteggFilterChain, Frontegg>> C getOrApply(C configurer) throws Exception {
        C existingConfig = (C) getConfigurer(configurer.getClass());
        if (existingConfig != null) {
            return existingConfig;
        }
        return apply(configurer);
    }

    public <C> void setSharedObject(Class<C> sharedType, C object) {
        super.setSharedObject(sharedType, object);
    }
}

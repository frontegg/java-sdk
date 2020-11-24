package com.frontegg.sdk.middleware.spring.core.configurers;

import com.frontegg.sdk.middleware.spring.core.DefaultFronteggFilterChain;
import com.frontegg.sdk.middleware.spring.core.FronteggAppConfigurerAdapter;
import com.frontegg.sdk.middleware.spring.core.builders.FronteggAppBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

public class CorsConfigurer<H extends FronteggAppBuilder<H>> extends FronteggAppConfigurerAdapter<DefaultFronteggFilterChain, H> {
    private CorsConfigurationSource configurationSource;

    private static final String CORS_CONFIGURATION_SOURCE_BEAN_NAME = "corsConfigurationSource";
    private static final String CORS_FILTER_BEAN_NAME = "corsFilter";

    public CorsConfigurer() {
    }

    public CorsConfigurer<H> configurationSource(
            CorsConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
        return this;
    }

    @Override
    public void configure(H frontegg) {
        ApplicationContext context = frontegg.getSharedObject(ApplicationContext.class);

        CorsFilter corsFilter = getCorsFilter(context);
        if (corsFilter == null) {
            throw new IllegalStateException("Please configure either a " +
                    CORS_FILTER_BEAN_NAME + " bean or a "
                    + CORS_CONFIGURATION_SOURCE_BEAN_NAME + "bean.");
        }
        frontegg.addFilter(corsFilter);
    }

    public H disable() {
        getBuilder().removeConfigurer(getClass());
        return getBuilder();
    }

    private CorsFilter getCorsFilter(ApplicationContext context) {
        if (this.configurationSource != null) {
            return new CorsFilter(this.configurationSource);
        }

        boolean containsCorsFilter = context
                .containsBeanDefinition(CORS_FILTER_BEAN_NAME);
        if (containsCorsFilter) {
            return context.getBean(CORS_FILTER_BEAN_NAME, CorsFilter.class);
        }

        boolean containsCorsSource = context.containsBean(CORS_CONFIGURATION_SOURCE_BEAN_NAME);
        if (containsCorsSource) {
            CorsConfigurationSource configurationSource = context.getBean(CORS_CONFIGURATION_SOURCE_BEAN_NAME,
                    CorsConfigurationSource.class);
            return new CorsFilter(configurationSource);
        }

        return null;
    }


}

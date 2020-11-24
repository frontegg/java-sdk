package com.frontegg.sdk.middleware.spring.core;

import org.springframework.web.WebApplicationInitializer;

public abstract class AbstractFronteggWebApplicationInitializer implements WebApplicationInitializer {


    public static final String DEFAULT_FILTER_NAME = "fronteggAppFilterChain";

    private final Class<?>[] configurationClasses;

    protected AbstractFronteggWebApplicationInitializer() {
        this.configurationClasses = null;
    }

}

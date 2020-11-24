package com.frontegg.sdk.middleware.spring.core.builders;

import com.frontegg.sdk.middleware.spring.core.DefaultFronteggFilterChain;
import com.frontegg.sdk.middleware.spring.core.configurers.FronteggConfigurer;

import javax.servlet.Filter;

public interface FronteggAppBuilder<H extends FronteggAppBuilder<H>> extends
        FronteggBuilder<DefaultFronteggFilterChain> {

    <C extends FronteggConfigurer<DefaultFronteggFilterChain, H>> C getConfigurer(Class<C> clazz);

    <C extends FronteggConfigurer<DefaultFronteggFilterChain, H>> C removeConfigurer(Class<C> clazz);

    H addFilter(Filter filter);

    <C> void setSharedObject(Class<C> sharedType, C object);

    <C> C getSharedObject(Class<C> sharedType);
}

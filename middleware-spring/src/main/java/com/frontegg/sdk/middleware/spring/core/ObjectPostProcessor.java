package com.frontegg.sdk.middleware.spring.core;

import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Allows initialization of Objects. Typically this is used to call the {@link Aware}
 * methods, {@link InitializingBean#afterPropertiesSet()}, and ensure that
 * {@link DisposableBean#destroy()} has been invoked.
 *
 * @param <T> the bound of the types of Objects this {@link ObjectPostProcessor} supports.
 *
 */
public interface ObjectPostProcessor<T> {

    /**
     * Initialize the object possibly returning a modified instance that should be used
     * instead.
     *
     * @param object the object to initialize
     * @return the initialized version of the object
     */
    <O extends T> O postProcess(O object);
}
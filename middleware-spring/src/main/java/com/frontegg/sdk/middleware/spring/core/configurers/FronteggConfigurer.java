package com.frontegg.sdk.middleware.spring.core.configurers;

import com.frontegg.sdk.middleware.spring.core.builders.FronteggBuilder;

public interface FronteggConfigurer<O, B extends FronteggBuilder<O>> {

    /**
     * Initialize the {@link FronteggBuilder}. Here only shared state should be created
     * and modified, but not properties on the {@link FronteggBuilder} used for building
     * the object. This ensures that the {@link #configure(FronteggBuilder)} method uses
     * the correct shared objects when building. Configurers should be applied here.
     *
     * @param builder
     * @throws Exception
     */
    void init(B builder) throws Exception;

    /**
     * Configure the {@link FronteggBuilder} by setting the necessary properties on the
     * {@link FronteggBuilder}.
     *
     * @param builder
     * @throws Exception
     */
    void configure(B builder) throws Exception;
}

package com.frontegg.sdk.middleware.spring.core.builders;

public interface FronteggBuilder<O> {

    /**
     * Builds the object and returns it or null.
     *
     * @return the Object to be built or null if the implementation allows it.
     * @throws Exception if an error occurred when building the Object
     */
    O build() throws Exception;

}

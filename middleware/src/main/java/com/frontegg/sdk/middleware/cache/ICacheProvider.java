package com.frontegg.sdk.middleware.cache;

public interface ICacheProvider {

    <T> T getFromCache(Class<T> clazz);
}

package com.frontegg.sdk.api.client;

import java.util.Optional;

public interface IApiClient {

    <T> Optional<T> get(String url, Class<T> clazz);
    <T,R> Optional<T> post(String url, Class<T> clazz,  R body);

}

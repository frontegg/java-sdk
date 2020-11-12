package com.frontegg.sdk.api.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

public interface IApiClient {

    <T> Optional<T> get(String url, Class<T> clazz);
    <T,R> Optional<T> post(String url, Class<T> clazz,  R body);

    <T> Optional<T> service(String url,
                            HttpServletRequest request,
                            HttpServletResponse response,
                            Map<String, String> proxyHeaders,
                            Class<T> clazz);
}

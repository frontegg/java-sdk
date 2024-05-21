package com.frontegg.sdk.api.client;

import com.frontegg.sdk.common.model.FronteggHttpResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

/**
 *  ApiClient
 */
public interface ApiClient {

    <T> Optional<T> get(String url, Class<T> clazz);
    <T> Optional<T> get(String url, Map<String,String> headers, Class<T> clazz);

    <T,R> FronteggHttpResponse<T> post(String url, Class<T> clazz, R body);
    <T,R> FronteggHttpResponse<T> post(String url, Class<T> clazz, Map<String, String> headers, R body);

    <T> FronteggHttpResponse<T> service(String url,
                                        HttpServletRequest request,
                                        HttpServletResponse response,
                                        Map<String, String> headers,
                                        Class<T> clazz);
}

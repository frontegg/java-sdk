package com.frontegg.sdk.api.client;

import com.frontegg.sdk.common.model.FronteggHttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

public interface IApiClient {

    <T> Optional<T> get(String url, Class<T> clazz);
    <T> Optional<T> get(String url, Map<String,String> headers, Class<T> clazz);

    <T,R> Optional<T> post(String url,
                           Class<T> clazz,
                           R body);

    <T,R> Optional<T> post(String url,
                           Class<T> clazz,
                           R body,
                           Map<String, String> headers);

    <T> FronteggHttpResponse<T> service(String url,
                                        HttpServletRequest request,
                                        HttpServletResponse response,
                                        Map<String, String> headers,
                                        Class<T> clazz);
}

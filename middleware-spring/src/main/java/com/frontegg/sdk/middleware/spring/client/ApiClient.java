package com.frontegg.sdk.middleware.spring.client;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.middleware.spring.executor.GetExecutor;
import com.frontegg.sdk.middleware.spring.executor.PostExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

public class ApiClient implements IApiClient {

    private static final String BASE_URL = "https://api.frontegg.com/";
    private RestTemplate restTemplate;
    private String url;
    private HttpHeaders httpHeaders = new HttpHeaders();

    public ApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public <T> Optional<T> get(Class<T> clazz, Map<String,String> queryParams) {
        return GetExecutor.execute(restTemplate, clazz, buildUrl(clazz), createHttpEntity());
    }

    private <T> String buildUrl(Class<T> clazz) {
        return BASE_URL;
    }

    private HttpEntity<Object> createHttpEntity() {

        HttpHeaders headers = new HttpHeaders();
        httpHeaders.putAll(httpHeaders);

        HttpEntity httpEntity = new HttpEntity(headers);
        return httpEntity;
    }

    private <R> HttpEntity<Object> createHttpEntity(R body) {

        HttpHeaders headers = new HttpHeaders();
        httpHeaders.putAll(httpHeaders);

        HttpEntity httpEntity = new HttpEntity(body, headers);
        return httpEntity;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public <T> Optional<T> get(String url, Class<T> clazz) {
        return GetExecutor.execute(restTemplate, clazz, buildUrl(clazz), createHttpEntity());
    }

    @Override
    public <T, R> Optional<T> post(String url, Class<T> clazz, R body) {
        return PostExecutor.execute(restTemplate, clazz, url, body);
    }
}

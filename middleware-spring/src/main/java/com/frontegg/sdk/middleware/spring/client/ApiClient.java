package com.frontegg.sdk.middleware.spring.client;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.middleware.spring.executor.GetExecutor;
import com.frontegg.sdk.middleware.spring.executor.PostExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
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
        headers.put("Content-Type", Arrays.asList("application/json"));
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


    private <R> HttpEntity<Object> createHttpEntity(HttpServletRequest request,
                                                    Map<String, String> proxyHeaders) {

        HttpHeaders headers = new HttpHeaders();
        httpHeaders.putAll(httpHeaders);
        for (String key : proxyHeaders.keySet()) {
            httpHeaders.put(key, Arrays.asList(proxyHeaders.get(key)));
        }

        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        return httpEntity;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public <T> Optional<T> get(String url, Class<T> clazz) {
        return GetExecutor.execute(restTemplate, clazz, url, createHttpEntity());
    }

    @Override
    public <T, R> Optional<T> post(String url, Class<T> clazz, R body) {
        return PostExecutor.execute(restTemplate, clazz, url, body);
    }

    @Override
    public <T> Optional<T> service(String url,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Map<String, String> proxyHeaders,
                                   Class<T> clazz) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (!StringHelper.isBlank(request.getQueryString())) {
            builder.query(request.getQueryString());
        }
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        return Optional.of(restTemplate.exchange(builder.toUriString(), method, createHttpEntity(request, proxyHeaders), clazz).getBody());
    }
}

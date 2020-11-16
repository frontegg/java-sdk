package com.frontegg.sdk.middleware.spring.client;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.common.model.FronteggHttpHeader;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.StringHelper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ApiClient implements IApiClient {

    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders = new HttpHeaders();

    public ApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpEntity<Object> createHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.APPLICATION_JSON_VALUE));
        httpHeaders.putAll(httpHeaders);

        HttpEntity httpEntity = new HttpEntity(headers);
        return httpEntity;
    }

    private <R> HttpEntity<Object> createHttpEntity(Map<String, String> proxyHeaders, R body) {
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.APPLICATION_JSON_VALUE));
        httpHeaders.putAll(httpHeaders);
        if (proxyHeaders != null) {
            for (String key : proxyHeaders.keySet()) {
                headers.put(key, Arrays.asList(proxyHeaders.get(key)));
            }
        }

        return body == null ? new HttpEntity(headers) : new HttpEntity(body, headers);
    }

    private HttpEntity<Object> createHttpEntity(HttpServletRequest request,
                                                Map<String, String> proxyHeaders) {

        HttpHeaders headers = new HttpHeaders();
        headers.putAll(httpHeaders);
        if (proxyHeaders != null) {
            for (String key : proxyHeaders.keySet()) {
                headers.put(key, Arrays.asList(proxyHeaders.get(key)));
            }
        }

        HttpEntity httpEntity;
        String body = getBody(request);
        if (body != null) {
            headers.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.APPLICATION_JSON_VALUE));
            headers.put(HttpHeaders.CONTENT_LENGTH, Arrays.asList(String.valueOf(body.length())));
            httpEntity = new HttpEntity(body, headers);
        } else {
            httpEntity = new HttpEntity(headers);
        }

        return httpEntity;
    }

    private String getBody(HttpServletRequest request) {
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        if (method == HttpMethod.POST || method == HttpMethod.POST || method == HttpMethod.POST) {
            try {
                return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public <T> Optional<T> get(String url, Class<T> clazz) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        ResponseEntity<T> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, createHttpEntity(), clazz);
        return Optional.of(responseEntity.getBody());
    }

    @Override
    public <T> Optional<T> get(String url, Map<String, String> headers, Class<T> clazz) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        ResponseEntity<T> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, createHttpEntity(headers, null), clazz);
        return Optional.of(responseEntity.getBody());
    }

    @Override
    public <T, R> Optional<T> post(String url, Class<T> clazz, R body) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        ResponseEntity<T> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, createHttpEntity(null, body), clazz);
        return Optional.of(responseEntity.getBody());
    }

    @Override
    public <T, R> Optional<T> post(String url, Class<T> clazz, R body, Map<String, String> headers) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        ResponseEntity<T> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, createHttpEntity(headers, null), clazz);
        return Optional.of(responseEntity.getBody());
    }

    @Override
    public <T> FronteggHttpResponse<T> service(String url,
                                               HttpServletRequest request,
                                               HttpServletResponse response,
                                               Map<String, String> headers,
                                               Class<T> clazz) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (!StringHelper.isBlank(request.getQueryString())) {
            builder.query(request.getQueryString());
        }

        HttpMethod method = HttpMethod.resolve(request.getMethod());
        ResponseEntity<T> responseEntity = restTemplate.exchange(builder.toUriString(), method, createHttpEntity(request, headers), clazz);

        return convert(responseEntity);
    }

    private <T> ResponseEntity<T> doService(String url, HttpMethod method, HttpEntity httpEntity, Class<T> clazz) {
        return restTemplate.exchange(url, method, httpEntity, clazz);
    }

    private static <T> FronteggHttpResponse<T> convert(ResponseEntity<T> responseEntity) {
        FronteggHttpResponse<T> response = new FronteggHttpResponse<>();
        response.setBody(responseEntity.getBody());
        response.setStatusCode(responseEntity.getStatusCodeValue());
        response.setHeaders(convertHeaders(responseEntity.getHeaders()));
        return response;
    }

    private static List<FronteggHttpHeader> convertHeaders(HttpHeaders headers) {
        List<FronteggHttpHeader> fronteggHttpHeaders = new ArrayList<>();
        Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
        for (Map.Entry<String, List<String>> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue().stream().collect(Collectors.joining(","));
            fronteggHttpHeaders.add(new FronteggHttpHeader(key, value));
        }
        return fronteggHttpHeaders;
    }
}

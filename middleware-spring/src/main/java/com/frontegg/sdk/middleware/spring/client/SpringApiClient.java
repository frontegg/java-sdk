package com.frontegg.sdk.middleware.spring.client;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.common.model.FronteggHttpHeader;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.middleware.authenticator.AuthenticationException;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SpringApiClient implements ApiClient {
    private static final Logger logger = LoggerFactory.getLogger(SpringApiClient.class);

    private RestTemplate restTemplate;

    public SpringApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
    public <T, R> FronteggHttpResponse<T> post(String url, Class<T> clazz, R body) {
       return post(url, clazz, null, body);
    }

    @Override
    public <T, R> FronteggHttpResponse<T> post(String url, Class<T> clazz, Map<String, String> headers, R body) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        try {
            ResponseEntity<T> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, createHttpEntity(headers, body), clazz);
            return convert(responseEntity);
        } catch (RestClientException ex) {
            throw new FronteggSDKException("frontegg sdk call fails with message", ex);
        }
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
        try {
            ResponseEntity<T> responseEntity = restTemplate.exchange(builder.toUriString(), method, createHttpEntity(request, headers), clazz);
            return convert(responseEntity);
        } catch (RestClientException ex) {

            if (ex instanceof HttpStatusCodeException) {
                if (((HttpStatusCodeException)ex).getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    throw new AuthenticationException(ex.getMessage(), ex);
                }
            }

            throw new FronteggSDKException("frontegg sdk call fails with message", ex);
        }
    }

    //region request helper methods

    private HttpEntity<Object> createHttpEntity() {
        return new HttpEntity(buildHttpHeaders(null));
    }

    private <R> HttpEntity<Object> createHttpEntity(Map<String, String> proxyHeaders, R body) {
        HttpHeaders headers = buildHttpHeaders(proxyHeaders);
        return buildHttpEntity(body, headers);
    }

    private HttpEntity<Object> createHttpEntity(HttpServletRequest request, Map<String, String> proxyHeaders) {
        HttpHeaders headers = buildHttpHeaders(proxyHeaders);
        populateRequestHeadersToApiRequest(headers, request);
        return buildHttpEntity(getBody(request), headers);
    }

    private <T> HttpEntity<T> buildHttpEntity(T body, HttpHeaders headers) {
        if (body == null) return new HttpEntity<>(headers);

        if (body instanceof String) {
            String strBody = (String) body;
            if (!StringHelper.isBlank(strBody)) {
                return buildHttpEntity(headers, strBody);
            }
        } else {
            try {
                Gson gson = new Gson();
                String strBody = gson.toJson(body);
                return buildHttpEntity(headers, strBody);
            } catch (Exception ex) {
                logger.error("unable to jsonify the request body of class -> " + body.getClass());
            }
        }

        return new HttpEntity<>(headers);
    }

    private <T> HttpEntity<T> buildHttpEntity(HttpHeaders headers, String strBody) {
        headers.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.APPLICATION_JSON_VALUE));
        headers.put(HttpHeaders.CONTENT_LENGTH, Arrays.asList(String.valueOf(strBody.length())));
        return new HttpEntity(strBody, headers);
    }

    private void populateRequestHeadersToApiRequest(HttpHeaders headers, HttpServletRequest request) {
        Enumeration enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String headerName = (String) enumeration.nextElement();
            if (headers.containsKey(headerName)) continue;

            String headerValue = request.getHeader(headerName);
            headers.add(headerName, headerValue);
        }
    }

    private HttpHeaders buildHttpHeaders(Map<String, String> headersMap) {
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.APPLICATION_JSON_VALUE));

        if (headersMap != null) {
            for (String key : headersMap.keySet()) {
                headers.add(key, headersMap.get(key));
            }
        }

        return headers;
    }

    private String getBody(HttpServletRequest request) {
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH) {
            try {
                return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            } catch (IOException e) {
                logger.error("Unable to read the body of request", e);
            }
        }
        return null;
    }
    //endregion

    //region response helper methods
    private <T> FronteggHttpResponse<T> convert(ResponseEntity<T> responseEntity) {
        FronteggHttpResponse<T> response = new FronteggHttpResponse<>();
        response.setBody(responseEntity.getBody());
        response.setStatusCode(responseEntity.getStatusCodeValue());
        response.setHeaders(convertHeaders(responseEntity.getHeaders()));
        return response;
    }

    private List<FronteggHttpHeader> convertHeaders(HttpHeaders headers) {
        List<FronteggHttpHeader> fronteggHttpHeaders = new ArrayList<>();
        Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
        for (Map.Entry<String, List<String>> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue().stream().collect(Collectors.joining(";"));
            fronteggHttpHeaders.add(new FronteggHttpHeader(key, value));
        }
        return fronteggHttpHeaders;
    }
    //endregion
}

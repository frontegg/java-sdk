package com.frontegg.sdk.middleware.spring.executor;

import com.frontegg.sdk.middleware.response.BaseResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

abstract class AbstractExecutor {

    private final RestTemplate restTemplate;
    private final Class<?> clazz;
    private final String url;
    private final HttpEntity<Object> httpEntity;

    public AbstractExecutor(RestTemplate restTemplate, Class<?> clazz, String url, HttpEntity<Object> httpEntity) {
        this.restTemplate = restTemplate;
        this.clazz = clazz;
        this.url = url;
        this.httpEntity = httpEntity;
    }

    protected abstract HttpMethod getMethod();

    protected abstract ParameterizedTypeReference<? extends BaseResponse> getParameterizedTypeReference(Class<?> clazz);

    protected abstract <R extends BaseResponse> Object getData(R baseResponse);


    Object execute() {
        final ResponseEntity<? extends BaseResponse> responseEntity  = exchange();
        validate(url, responseEntity);
        return getData(responseEntity.getBody());

    }

    private ResponseEntity<? extends BaseResponse> exchange() {
        return restTemplate.exchange(url, getMethod(), httpEntity, getParameterizedTypeReference(clazz));
    }


    private void validate(String url, ResponseEntity<? extends BaseResponse> responseEntity) {
        if (responseEntity.getStatusCode().isError()) {

        }
    }

    Object executePost(Object body) {
        final ResponseEntity<? extends BaseResponse> responseEntity  = doPost(body);
        validate(url, responseEntity);
        return getData(responseEntity.getBody());
    }

    private ResponseEntity<? extends BaseResponse> doPost(Object body) {
        return restTemplate.postForEntity(url, new HttpEntity<>(body), BaseResponse.class);
    }


}

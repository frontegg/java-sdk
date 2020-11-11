package com.frontegg.sdk.middleware.spring.executor;

import com.frontegg.sdk.middleware.response.ApiObjectListResponse;
import com.frontegg.sdk.middleware.response.ApiObjectResponse;
import com.frontegg.sdk.middleware.response.BaseResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class PostExecutor extends AbstractExecutor {

    private PostExecutor(RestTemplate restTemplate, Class<?> clazz, String url, HttpEntity<Object> httpEntity) {
        super(restTemplate, clazz, url, httpEntity);
    }

    public static <T> Optional<T> execute(RestTemplate restTemplate, Class<T> clazz, String url, HttpEntity<Object> httpEntity) {
        return (Optional<T>) Optional.ofNullable(new PostExecutor(restTemplate, clazz, url, httpEntity).execute());
    }

    public static <T> Optional<T> execute(RestTemplate restTemplate, Class<T> clazz, String url, Object body) {
        return (Optional<T>) Optional.ofNullable(new PostExecutor(restTemplate, clazz, url, null).executePost(body));
    }

    protected HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    protected ParameterizedTypeReference<? extends BaseResponse> getParameterizedTypeReference(Class<?> clazz) {
        return ParameterizedTypeReference.forType(ResolvableType.forClassWithGenerics(ApiObjectListResponse.class, clazz).getType());
    }

    protected <R extends BaseResponse> Object getData(R baseResponse) {
        if (baseResponse instanceof ApiObjectResponse) {
            return ((ApiObjectResponse) baseResponse).getData();
        }

        return baseResponse;
    }
}
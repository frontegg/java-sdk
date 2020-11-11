package com.frontegg.sdk.middleware.spring.client;

import org.springframework.web.client.RestTemplate;

public class ApiClientFactory {

    private final RestTemplate restTemplate;

    public ApiClientFactory(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public final ApiClient create(String url) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setUrl(url);
        return apiClient;
    }

}

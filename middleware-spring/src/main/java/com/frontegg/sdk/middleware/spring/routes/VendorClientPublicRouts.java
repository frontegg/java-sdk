package com.frontegg.sdk.middleware.spring.routes;

import java.util.List;
import java.util.Map;

public class VendorClientPublicRouts {
    private String method;
    private String url;
    private String description;
    private List<Map<String, String>> withQueryParams;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Map<String, String>> getWithQueryParams() {
        return withQueryParams;
    }

    public void setWithQueryParams(List<Map<String, String>> withQueryParams) {
        this.withQueryParams = withQueryParams;
    }
}

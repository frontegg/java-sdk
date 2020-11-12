package com.frontegg.sdk.middleware.spring.routes;

import java.util.List;

public class VendorClientPublicRouts {
    private String method;
    private String url;
    private String description;
    private List<KeyValPair> withQueryParams;

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

    public List<KeyValPair> getWithQueryParams() {
        return withQueryParams;
    }

    public void setWithQueryParams(List<KeyValPair> withQueryParams) {
        this.withQueryParams = withQueryParams;
    }
}

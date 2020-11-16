package com.frontegg.sdk.common.model;

import java.util.List;

public class FronteggHttpResponse<T> {

    private List<FronteggHttpHeader> headers;
    private T body;
    private int statusCode;

    public List<FronteggHttpHeader> getHeaders() {
        return headers;
    }

    public void setHeaders(List<FronteggHttpHeader> headers) {
        this.headers = headers;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "FronteggHttpResponse{" +
                "headers=" + headers +
                ", body=" + body +
                ", statusCode=" + statusCode +
                '}';
    }
}

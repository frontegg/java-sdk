package com.frontegg.sdk.common.model;

import java.util.List;

public class FronteggHttpResponse<T> {

    private List<FronteggHttpHeader> headers;
    private T body;
    private int statusCode;

    public List<FronteggHttpHeader> getHeaders() {
        return this.headers;
    }

    public void setHeaders(List<FronteggHttpHeader> headers) {
        this.headers = headers;
    }

    public T getBody() {
        return this.body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "FronteggHttpResponse{" +
               "headers=" + this.headers +
               ", body=" + this.body +
               ", statusCode=" + this.statusCode +
               '}';
    }
}

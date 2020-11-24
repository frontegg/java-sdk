package com.frontegg.sdk.middleware.spring.core.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FronteggHttpRequestResponseHolder {

    private HttpServletRequest request;
    private HttpServletResponse response;

    public FronteggHttpRequestResponseHolder(HttpServletRequest request,
                                            HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}

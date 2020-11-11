package com.frontegg.sdk.middleware.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseResponse {

    private int status;
    private String message;
    private Object headersSent;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getHeadersSent() {
        return headersSent;
    }

    public void setHeadersSent(Object headersSent) {
        this.headersSent = headersSent;
    }
}

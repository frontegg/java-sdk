package com.frontegg.sdk.common.exception;

public class FronteggApiException extends Exception {

    public FronteggApiException() {
    }

    public FronteggApiException(String message) {
        super(message);
    }

    public FronteggApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

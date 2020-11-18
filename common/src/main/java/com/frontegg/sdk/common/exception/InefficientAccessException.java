package com.frontegg.sdk.common.exception;

public class InefficientAccessException extends RuntimeException {

    public InefficientAccessException() {
    }

    public InefficientAccessException(String message) {
        super(message);
    }

    public InefficientAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

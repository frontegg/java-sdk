package com.frontegg.sdk.common.exception;

public class InsufficientAccessException extends RuntimeException {

    public InsufficientAccessException() {
    }

    public InsufficientAccessException(String message) {
        super(message);
    }

    public InsufficientAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

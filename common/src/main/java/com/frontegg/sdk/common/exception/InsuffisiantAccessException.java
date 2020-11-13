package com.frontegg.sdk.common.exception;

public class InsuffisiantAccessException extends RuntimeException {

    public InsuffisiantAccessException() {
    }

    public InsuffisiantAccessException(String message) {
        super(message);
    }

    public InsuffisiantAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

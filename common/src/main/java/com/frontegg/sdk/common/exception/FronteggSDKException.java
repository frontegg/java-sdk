package com.frontegg.sdk.common.exception;

public class FronteggSDKException extends RuntimeException {

    public FronteggSDKException() {
    }

    public FronteggSDKException(String message) {
        super(message);
    }

    public FronteggSDKException(String message, Throwable cause) {
        super(message, cause);
    }
}

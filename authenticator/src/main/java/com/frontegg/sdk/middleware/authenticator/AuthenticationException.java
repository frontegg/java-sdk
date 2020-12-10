package com.frontegg.sdk.middleware.authenticator;

import com.frontegg.sdk.common.exception.FronteggHttpException;

public class AuthenticationException extends FronteggHttpException {
    private static final int UNAUTHORIZED = 401;
    public AuthenticationException(String message) {
        super(UNAUTHORIZED, message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(UNAUTHORIZED, message, cause);
    }
}

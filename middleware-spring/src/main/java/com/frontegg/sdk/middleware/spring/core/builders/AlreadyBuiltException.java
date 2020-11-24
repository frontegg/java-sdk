package com.frontegg.sdk.middleware.spring.core.builders;

public class AlreadyBuiltException extends IllegalStateException {

    public AlreadyBuiltException(String message) {
        super(message);
    }

    private static final long serialVersionUID = -5891004752785553015L;
}

package com.frontegg.sdk.common.exception;

public class FronteggHttpException extends FronteggSDKException {
   private int status;


    public FronteggHttpException(int status, String message) {
        super(message);
        this.status = status;
    }

    public FronteggHttpException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }
}

package com.frontegg.ws.sample;

import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.common.exception.InefficientAccessException;
import com.frontegg.sdk.common.exception.InvalidParameterException;
import com.frontegg.sdk.middleware.authenticator.AuthenticationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class FronteggControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<Object> handleUnAuthorized(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, "Unauthorized!", new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }
    @ExceptionHandler(InefficientAccessException.class)
    protected ResponseEntity<Object> handlePermissionDenied(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, "Permission Denied", new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(InvalidParameterException.class)
    protected ResponseEntity<Object> handleBadRequests(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(FronteggSDKException.class)
    protected ResponseEntity<Object> handleInternalErrors(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleInternalErrors(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "Something went wrong!", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


}

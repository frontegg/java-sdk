package com.frontegg.sdk.middleware.authentication;

import javax.servlet.http.HttpServletRequest;

public interface FronteggAuthenticationService {

    /**
     * Validate request is authorized
     */
    void withAuthentication(HttpServletRequest request);

    /**
     * Authenticate Frontegg application If not authenticated yet.
     */
    void authenticateFronteggApplicationIfNeeded();
}

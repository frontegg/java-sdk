package com.frontegg.sdk.middleware.authentication;

import javax.servlet.http.HttpServletRequest;

public interface FronteggAuthenticationService {

    void withAuthentication(HttpServletRequest request);

    void authenticateApp();
}

package com.frontegg.sdk.middleware.authentication;

import javax.servlet.http.HttpServletRequest;

public interface IAuthenticationService {

    void withAuthentication(HttpServletRequest request);

    void authenticateApp();
}

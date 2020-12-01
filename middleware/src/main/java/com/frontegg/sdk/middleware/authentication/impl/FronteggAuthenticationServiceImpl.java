package com.frontegg.sdk.middleware.authentication.impl;

import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.middleware.authentication.FronteggAuthenticationService;
import com.frontegg.sdk.middleware.identity.FronteggIdentityService;
import com.frontegg.sdk.middleware.authenticator.Authentication;
import com.frontegg.sdk.middleware.authenticator.AuthenticationException;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.FronteggContextHolder;

import javax.servlet.http.HttpServletRequest;

public class FronteggAuthenticationServiceImpl implements FronteggAuthenticationService {

    private FronteggAuthenticator authenticator;
    private FronteggIdentityService fronteggIdentityService;

    public FronteggAuthenticationServiceImpl(FronteggAuthenticator authenticator, FronteggIdentityService fronteggIdentityService) {
        this.authenticator = authenticator;
        this.fronteggIdentityService = fronteggIdentityService;
    }

    public void withAuthentication(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("authorization");
        if (StringHelper.isBlank(authorizationHeader)) {
           throw new AuthenticationException("Authentication is required URL - " + request.getRequestURI());
        }

        String token = authorizationHeader.replace("Bearer ", "");

        fronteggIdentityService.verifyToken(token);
     }

    public void authenticateApp() {
        Authentication authentication = authenticator.authenticate();
        FronteggContext fronteggContext = FronteggContextHolder.getContext();
        fronteggContext.setAuthentication(authentication);
        FronteggContextHolder.setContext(fronteggContext);
    }
}

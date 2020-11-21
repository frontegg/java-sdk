package com.frontegg.sdk.middleware.authentication.impl;

import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.middleware.authentication.IAuthenticationService;
import com.frontegg.sdk.middleware.identity.IIdentityService;
import com.frontegg.sdk.middleware.authenticator.Authentication;
import com.frontegg.sdk.middleware.authenticator.AuthenticationException;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.FronteggContextHolder;

import javax.servlet.http.HttpServletRequest;

public class AuthenticationService implements IAuthenticationService {

    private FronteggAuthenticator authenticator;
    private IIdentityService identityService;

    public AuthenticationService(FronteggAuthenticator authenticator, IIdentityService identityService) {
        this.authenticator = authenticator;
        this.identityService = identityService;
    }

    public void withAuthentication(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("authorization");
        if (StringHelper.isBlank(authorizationHeader)) {
           throw new AuthenticationException("Authentication is required URL - " + request.getRequestURI());
        }

        String token = authorizationHeader.replace("Bearer ", "");

        identityService.verifyToken(token);
     }

    public void authenticateApp() {
        Authentication authentication = authenticator.authenticate();
        FronteggContext fronteggContext = FronteggContextHolder.getContext();
        fronteggContext.setAuthentication(authentication);
        FronteggContextHolder.setContext(fronteggContext);
    }
}

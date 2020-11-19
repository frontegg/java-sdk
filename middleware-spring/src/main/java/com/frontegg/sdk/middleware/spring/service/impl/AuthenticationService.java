package com.frontegg.sdk.middleware.spring.service.impl;

import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.middleware.IIdentityService;
import com.frontegg.sdk.middleware.authenticator.Authentication;
import com.frontegg.sdk.middleware.authenticator.AuthenticationException;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.spring.context.FronteggContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AuthenticationService {

    @Autowired
    private FronteggAuthenticator authenticator;

    @Autowired
    private IIdentityService identityService;

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

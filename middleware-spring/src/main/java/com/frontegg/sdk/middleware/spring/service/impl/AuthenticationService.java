package com.frontegg.sdk.middleware.spring.service.impl;

import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.middleware.authenticator.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AuthenticationService {

    public void withAuthentication(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("authorization");
        if (StringHelper.isBlank(authorizationHeader)) {
           throw new AuthenticationException();
        }

        String token = authorizationHeader.replace("Bearer ", "");

        verifyToken(token);



        // Store the decoded user on the request

        //req.user = user;
        //req.user.id = user.sub; // The subject of the token (OpenID token) is saved on the req.user as well for easier readability
     }

    private void verifyToken(String token) {

    }
}

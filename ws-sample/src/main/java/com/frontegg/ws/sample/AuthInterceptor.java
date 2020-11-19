package com.frontegg.ws.sample;

import com.frontegg.sdk.middleware.routes.IFronteggRouteService;
import com.frontegg.sdk.middleware.spring.service.impl.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    private static Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private IFronteggRouteService fronteggRouteService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        authenticationService.authenticateApp();

        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(204);
            return false;
        }

        if (!isFronteggPublicRoute(request)) {
            logger.debug("will pass request threw the auth middleware");
            authenticationService.withAuthentication(request);
        }

        return super.preHandle(request, response, handler);
    }

    private boolean isFronteggPublicRoute(HttpServletRequest request) {
        return fronteggRouteService.isFronteggPublicRoute(request);
    }
}

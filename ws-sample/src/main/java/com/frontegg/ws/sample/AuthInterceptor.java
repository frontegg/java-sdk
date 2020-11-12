package com.frontegg.ws.sample;

import com.frontegg.sdk.middleware.spring.service.impl.AuthenticationService;
import com.frontegg.sdk.middleware.spring.service.IFronteggRouteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
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
        if (isFronteggPublicRoute(request)) {
            logger.debug("will pass request threw the auth middleware");
            authenticationService.withAuthentication(request);
            if (response.containsHeader("headersSent")) {
                // response was already sent from the middleware, we have nothing left to do
                return false;
            }
        }

        return super.preHandle(request, response, handler);
    }

    private boolean isFronteggPublicRoute(HttpServletRequest request) {
        return fronteggRouteService.isFronteggPublicRoute(request);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        super.afterConcurrentHandlingStarted(request, response, handler);
    }
}

package com.frontegg.sdk.middleware.spring.filter;

import com.frontegg.sdk.middleware.authentication.IAuthenticationService;
import com.frontegg.sdk.middleware.routes.IFronteggRouteService;
import com.frontegg.sdk.middleware.authentication.impl.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FronteggAuthenticationFilter extends GenericFilterBean {
    private static Logger logger = LoggerFactory.getLogger(FronteggAuthenticationFilter.class);

    private IAuthenticationService authenticationService;
    private IFronteggRouteService fronteggRouteService;

    public FronteggAuthenticationFilter(IAuthenticationService authenticationService, IFronteggRouteService fronteggRouteService) {
        this.authenticationService = authenticationService;
        this.fronteggRouteService = fronteggRouteService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        authenticationService.authenticateApp();

        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(204);
            return;
        }

        if (!isFronteggPublicRoute(request)) {
            logger.debug("will pass request threw the auth middleware");
            authenticationService.withAuthentication(request);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isFronteggPublicRoute(HttpServletRequest request) {
        return fronteggRouteService.isFronteggPublicRoute(request);
    }

}

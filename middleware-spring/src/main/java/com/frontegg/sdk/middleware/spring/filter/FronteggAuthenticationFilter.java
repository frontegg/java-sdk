package com.frontegg.sdk.middleware.spring.filter;

import com.frontegg.sdk.middleware.authentication.IFronteggAuthenticationService;
import com.frontegg.sdk.middleware.routes.IFronteggRouteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FronteggAuthenticationFilter extends FronteggBaseFilter {
    private static Logger logger = LoggerFactory.getLogger(FronteggAuthenticationFilter.class);

    private IFronteggAuthenticationService authenticationService;
    private IFronteggRouteService fronteggRouteService;

    public FronteggAuthenticationFilter(IFronteggAuthenticationService authenticationService, IFronteggRouteService fronteggRouteService) {
        this.authenticationService = authenticationService;
        this.fronteggRouteService = fronteggRouteService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (requestMatcher.matches(request)) {

            authenticationService.authenticateApp();

            if (request.getMethod().equals("OPTIONS")) {
                response.setStatus(204);
                return;
            }

            if (!isFronteggPublicRoute(request)) {
                logger.debug("will pass request threw the auth middleware");
                authenticationService.withAuthentication(request);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isFronteggPublicRoute(HttpServletRequest request) {
        return fronteggRouteService.isFronteggPublicRoute(request);
    }
}

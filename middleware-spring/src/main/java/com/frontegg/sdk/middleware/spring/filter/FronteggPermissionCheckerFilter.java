package com.frontegg.sdk.middleware.spring.filter;

import com.frontegg.sdk.common.exception.InefficientAccessException;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.permission.IPermissionEvaluator;
import com.frontegg.sdk.middleware.context.FronteggContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class FronteggPermissionCheckerFilter extends GenericFilterBean {
    private static Logger logger = LoggerFactory.getLogger(FronteggPermissionCheckerFilter.class);

    private IPermissionEvaluator permissionEvaluator;

    public FronteggPermissionCheckerFilter(IPermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            FronteggContext fronteggContext = FronteggContextHolder.getContext();
            permissionEvaluator.validatePermissions(request, fronteggContext);
        } catch (InefficientAccessException ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        }

       filterChain.doFilter(servletRequest, servletResponse);
    }
}

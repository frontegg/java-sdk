package com.frontegg.ws.sample;

import com.frontegg.sdk.common.exception.InefficientAccessException;
import com.frontegg.sdk.middleware.IPermissionEvaluator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.spring.context.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//TODO remove
//@Component
public class PermissionCheckerInterceptor extends HandlerInterceptorAdapter {

    private static Logger logger = LoggerFactory.getLogger(PermissionCheckerInterceptor.class);

    @Autowired
    private IPermissionEvaluator permissionEvaluator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {
            FronteggContext fronteggContext = ContextHolder.getRequestContext().getFronteggContext();
            permissionEvaluator.validatePermissions(request, fronteggContext);
        } catch (InefficientAccessException ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        }

        return super.preHandle(request, response, handler);
    }
}

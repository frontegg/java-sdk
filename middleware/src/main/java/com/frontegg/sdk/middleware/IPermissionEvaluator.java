package com.frontegg.sdk.middleware;

import com.frontegg.sdk.middleware.context.FronteggContext;

import javax.servlet.http.HttpServletRequest;

public interface IPermissionEvaluator {

    void validatePermissions(HttpServletRequest request, FronteggContext context);
}

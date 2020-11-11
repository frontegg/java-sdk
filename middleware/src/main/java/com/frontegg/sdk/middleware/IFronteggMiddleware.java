package com.frontegg.sdk.middleware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IFronteggMiddleware {

    void doProcess(HttpServletRequest request, HttpServletResponse response);
}

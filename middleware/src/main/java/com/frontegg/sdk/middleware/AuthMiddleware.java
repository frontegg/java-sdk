package com.frontegg.sdk.middleware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthMiddleware {

    void callMiddleware(HttpServletRequest request, HttpServletResponse response);
}

package com.frontegg.sdk.middleware.spring.context;

import com.frontegg.sdk.middleware.context.FronteggContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface FronteggContextRepository {

    FronteggContext loadContext(FronteggHttpRequestResponseHolder holder);

    void saveContext(FronteggContext contextAfterChainExecution, HttpServletRequest request, HttpServletResponse response);
}

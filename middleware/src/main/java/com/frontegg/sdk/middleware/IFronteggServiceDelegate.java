package com.frontegg.sdk.middleware;

import com.frontegg.sdk.common.model.FronteggHttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IFronteggServiceDelegate {

    FronteggHttpResponse<Object> doProcess(HttpServletRequest request, HttpServletResponse response);
}

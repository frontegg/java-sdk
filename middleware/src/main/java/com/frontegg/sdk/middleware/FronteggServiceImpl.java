package com.frontegg.sdk.middleware;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.HttpHelper;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authenticator.AuthenticationException;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.FronteggContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.frontegg.sdk.common.util.HttpHelper.*;

public class FronteggServiceImpl implements FronteggService {

    private static final Logger logger = LoggerFactory.getLogger(FronteggServiceImpl.class);

    private FronteggConfig config;
    private ApiClient apiClient;
    private FronteggAuthenticator authenticator;
    private FronteggOptions fronteggOptions;

    public FronteggServiceImpl(FronteggConfig config,
                               ApiClient apiClient,
                               FronteggAuthenticator authenticator,
                               FronteggOptions fronteggOptions) {

        this.config = config;
        this.apiClient = apiClient;
        this.authenticator = authenticator;
        this.fronteggOptions = fronteggOptions;
    }

    public FronteggHttpResponse<Object> doProcess(HttpServletRequest request, HttpServletResponse response) {

        FronteggContext context = FronteggContextHolder.getContext();
        logger.debug("going to proxy request - {} to  {} ", request.getRequestURI(), config.getUrlConfig().getBaseUrl());
        Map<String, String> headers = initHeaders(request, context);

        String requestUrl = request.getRequestURI().substring(context.getFronteggBasePath().length());
        String url = config.getUrlConfig().getBaseUrl() + requestUrl;

        FronteggHttpResponse<Object> val = initiateRequest(url, request, response, headers);

        return val;
    }

    @Override
    public void authorizeApplication() {
        logger.warn("going to refresh authentication");
        authenticator.refreshAuthentication();
        logger.warn("refreshed authentication");
    }

    private FronteggHttpResponse<Object> initiateRequest(String url,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response,
                                                         Map<String, String> headers){
        FronteggHttpResponse<Object> val;
        val = apiClient.service(url, request, response, headers, Object.class);

        //UnAuthorized
        if (val.getStatusCode() == 401) {
            throw new AuthenticationException("Application is not authorized");
        }

        //Rewrite Cookies
        if (!StringHelper.isBlank(fronteggOptions.getCookieDomainRewrite())) {
            cookieDomainRewrite(val, request, response);
        }

        return val;
    }

    private void cookieDomainRewrite(FronteggHttpResponse<Object> fronteggHttpResponse, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String host = HttpHelper.getHeader(request, FRONTEGG_HEADER_HOST);
        if (!StringHelper.isBlank(host)) {
            for (Cookie cookie : cookies) {
                if (cookie.getDomain().equals(host)) {
                    cookie.setDomain(fronteggOptions.getCookieDomainRewrite());
                }
            }
        }
    }

    private Map<String, String> initHeaders(HttpServletRequest request, FronteggContext context) {
        Map<String, String> headers = new HashMap<>();
        headers.put(FRONTEGG_HEADER_ACCESS_TOKEN, authenticator.getAccessToken());
        headers.put(FRONTEGG_HEADER_TENANT_ID, context.getTenantId() == null  ? "" : context.getTenantId());
        headers.put(FRONTEGG_HEADER_USER_ID, context.getUserId() == null  ? "" : context.getUserId());
        headers.put(FRONTEGG_HEADER_VENDOR_HOST, HttpHelper.getHostnameFromRequest(request));
        return headers;
    }
}

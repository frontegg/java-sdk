package com.frontegg.sdk.middleware;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.HttpUtil;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authenticator.Authentication;
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

import static com.frontegg.sdk.common.util.HttpUtil.*;

public class FronteggService implements IFronteggService {

    private static final Logger logger = LoggerFactory.getLogger(FronteggService.class);

    private FronteggConfig config;
    private IApiClient apiClient;
    private FronteggAuthenticator authenticator;
    private FronteggOptions fronteggOptions;

    public FronteggService(FronteggConfig config,
                           IApiClient apiClient,
                           FronteggAuthenticator authenticator,
                           FronteggOptions fronteggOptions) {

        this.config = config;
        this.apiClient = apiClient;
        this.authenticator = authenticator;
        this.fronteggOptions = fronteggOptions;
    }

    public FronteggHttpResponse<Object> doProcess(HttpServletRequest request, HttpServletResponse response) {

        FronteggContext context = FronteggContextHolder.getContext();
        logger.info("going to proxy request - " + request.getRequestURI() + " to  " + config.getUrlConfig().getBaseUrl());
        Map<String, String> headers = initHeaders(request, context);

        String requestUrl = HttpUtil.getRequestUrl(request.getRequestURI(), context.getFronteggBasePath());
        String url = config.getUrlConfig().getBaseUrl() + requestUrl;

        FronteggHttpResponse<Object> val = initiateRequest(url, request, response, headers);
        logger.info("Response  = " + val.toString());

        return val;
    }

    @Override
    public void authorizeApplication() {
        logger.warn("going to refresh authentication");
        authenticator.refreshAuthentication();
        logger.warn("refreshed authentication");
    }

    private FronteggHttpResponse<Object> initiateRequest(String url, HttpServletRequest request, HttpServletResponse response, Map<String, String> headers){
        FronteggHttpResponse<Object> val;
        val = apiClient.service(url, request, response, headers, Object.class);

        //UnAuthorized
        if (val.getStatusCode() == 401) {
            throw new AuthenticationException("Application is not authorized");
        }

        //Cors header management
        if (fronteggOptions.isDisableCors()) {
            HttpUtil.deleteHeaders(response,
                    ACCESS_CONTROL_REQUEST_METHOD,
                    ACCESS_CONTROL_REQUEST_HEADERS,
                    ACCESS_CONTROL_ALLOW_ORIGIN,
                    ACCESS_CONTROL_ALLOW_CREDENTIALS
            );
        } else {
            enableCors(val, response);
        }

        //Rewrite Cookies
        if (!StringHelper.isBlank(fronteggOptions.getCookieDomainRewrite())) {
            cookieDomainRewrite(val, request, response);
        }

        return val;
    }

    private void cookieDomainRewrite(FronteggHttpResponse<Object> fronteggHttpResponse, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String host = HttpUtil.getHeader(request, "host");
        if (!StringHelper.isBlank(host)) {
            for (Cookie cookie : cookies) {
                if (cookie.getDomain().equals(host)) {
                    cookie.setDomain(fronteggOptions.getCookieDomainRewrite());
                }
            }
        }
    }

    private Map<String, String> initHeaders(HttpServletRequest request, FronteggContext context) {
        Authentication authentication = FronteggContextHolder.getContext().getAuthentication();
        Map<String, String> headers = new HashMap<>();
        headers.put(FRONTEGG_HEADER_ACCESS_TOKEN, authentication.getAccessToken());
        headers.put(FRONTEGG_HEADER_TENANT_ID, context.getTenantId() == null  ? "" : context.getTenantId());
        headers.put(FRONTEGG_HEADER_USER_ID, context.getUserId() == null  ? "" : context.getUserId());
        headers.put(FRONTEGG_HEADER_VENDOR_HOST, HttpUtil.getHostnameFromRequest(request));
        return headers;
    }

    void enableCors(FronteggHttpResponse<Object> fronteggHttpResponse, HttpServletResponse response) {
        HttpUtil.replaceHeader(fronteggHttpResponse.getHeaders(), response, ACCESS_CONTROL_REQUEST_METHOD);
        HttpUtil.replaceHeader(fronteggHttpResponse.getHeaders(), response, ACCESS_CONTROL_REQUEST_HEADERS);
        HttpUtil.replaceHeader(fronteggHttpResponse.getHeaders(), response, ORIGIN, ACCESS_CONTROL_ALLOW_ORIGIN);
    }
}

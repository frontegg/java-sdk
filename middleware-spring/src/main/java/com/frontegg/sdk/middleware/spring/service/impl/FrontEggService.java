package com.frontegg.sdk.middleware.spring.service.impl;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.HttpUtil;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.IFronteggService;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.RequestContext;
import com.frontegg.sdk.middleware.spring.context.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.frontegg.sdk.common.util.HttpUtil.*;

@Service
public class FrontEggService implements IFronteggService {

    private static final Logger logger = LoggerFactory.getLogger(FrontEggService.class);

    @Autowired
    private FronteggConfig config;
    @Autowired
    private IApiClient apiClient;
    @Autowired
    private FronteggAuthenticator authenticator;

    @Value("${frontegg.settings.disableCors:#{true}}")
    private boolean disableCors;
    @Value("${frontegg.settings.maxRetries:#{3}}")
    private int maxRetries;
    @Value("${frontegg.settings.cookieDomainRewrite:#{''}}")
    private String cookieDomainRewrite;

    public FronteggHttpResponse<Object> doProcess(HttpServletRequest request, HttpServletResponse response) {

        RequestContext context = ContextHolder.getRequestContext();
        logger.info("going to proxy request - " + request.getRequestURI() + " to  " + config.getUrlConfig().getBaseUrl());
        Map<String, String> headers = initHeaders(request, context.getFronteggContext());

        String url = config.getUrlConfig().getBaseUrl() + context.getRequestPath();

        FronteggHttpResponse<Object> val = initiateRequest(url, request, response, headers);
        logger.info("Response  = " + val.toString());

        return val;
    }

    private FronteggHttpResponse<Object> initiateRequest(String url, HttpServletRequest request, HttpServletResponse response, Map<String, String> headers){
        try {
            FronteggHttpResponse<Object> val = apiClient.service(url, request, response, headers, Object.class);

            //Cors header management
            if (disableCors) {
                HttpUtil.deleteHeaders(response,
                        HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
                        HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
                        HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS
                );
            } else {
                enableCors(val, response);
            }

            //UnAuthorized
            if (val.getStatusCode() == 401) {

                FronteggContext context = ContextHolder.getRequestContext().getFronteggContext();
                context.setRetryCount(context.getRetryCount() + 1);
                logger.info(url + "failed with authentication error from proxy - retryCount - " +  context.getRetryCount());

                if (context.getRetryCount() <= maxRetries) {
                    logger.warn("going to refresh authentication");
                    authenticator.refreshAuthentication();
                    logger.warn("refreshed authentication");

                    return initiateRequest(url, request, response, headers);
                }

            }

            //Rewrite Cookies
            if (!StringHelper.isBlank(cookieDomainRewrite)) {
                cookieDomainRewrite(val, request, response);
            }

            return val;
        } catch (Exception ex) {
            return handleError(url, request, response, headers, ex);
        }
    }

    private FronteggHttpResponse<Object> handleError(String url, HttpServletRequest request, HttpServletResponse response, Map<String, String> headers, Exception ex) {
        FronteggContext context = ContextHolder.getRequestContext().getFronteggContext();
        logger.error("Failed proxy request to - " + url);
        context.setRetryCount(context.getRetryCount() + 1);
        logger.info("retry count of " + url + " = " + context.getRetryCount());

        if (context.getRetryCount() >= maxRetries) {
            throw new FronteggSDKException("Frontegg request failed", ex);
        }

        return initiateRequest(url, request, response, headers);
    }

    private void cookieDomainRewrite(FronteggHttpResponse<Object> fronteggHttpResponse, HttpServletRequest request, HttpServletResponse response) {
        if (!StringHelper.isBlank(cookieDomainRewrite)) {
            Cookie[] cookies = request.getCookies();
            String host = HttpUtil.getHeader(request, "host");
            if (!StringHelper.isBlank(host)) {
                for (Cookie cookie : cookies) {
                    if (cookie.getDomain().equals(host)) {
                        cookie.setDomain(cookieDomainRewrite);
                    }
                }
            }
        }
    }

    private Map<String, String> initHeaders(HttpServletRequest request, FronteggContext context) {
        String token = ContextHolder.getAuthentication().getAccessToken();
        Map<String, String> headers = new HashMap<>();
        headers.put(FRONTEGG_HEADER_ACCESS_TOKEN, token);
        headers.put(FRONTEGG_HEADER_TENANT_ID, context.getTenantId() == null  ? "" : context.getTenantId());
        headers.put(FRONTEGG_HEADER_USER_ID, context.getUserId() == null  ? "" : context.getUserId());
        headers.put(FRONTEGG_HEADER_VENDOR_HOST, HttpUtil.getHostnameFromRequest(request));
        return headers;
    }

    void enableCors(FronteggHttpResponse<Object> fronteggHttpResponse, HttpServletResponse response) {
        HttpUtil.replaceHeader(fronteggHttpResponse.getHeaders(), response, HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD);
        HttpUtil.replaceHeader(fronteggHttpResponse.getHeaders(), response, HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
        HttpUtil.replaceHeader(fronteggHttpResponse.getHeaders(), response, HttpHeaders.ORIGIN, HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
    }
}

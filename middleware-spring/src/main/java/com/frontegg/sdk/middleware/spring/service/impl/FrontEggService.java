package com.frontegg.sdk.middleware.spring.service.impl;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.common.exception.InvalidParameterException;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.IFronteggService;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.RequestContext;
import com.frontegg.sdk.middleware.spring.context.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class FrontEggService implements IFronteggService {

    private static final Logger logger = LoggerFactory.getLogger(FrontEggService.class);

    @Autowired
    private FronteggConfig config;
    @Autowired
    private IApiClient apiClient;

    @Value("${frontegg.settings.disableCors:#{true}}")
    private boolean disableCors;
    @Value("${frontegg.settings.maxRetries:#{3}}")
    private int maxRetries;
    @Value("${frontegg.settings.cookieDomainRewrite:#{''}}")
    private String cookieDomainRewrite;

    public Object doProcess(HttpServletRequest request, HttpServletResponse response) {

        //TODO validate permissions
//        try {
//            await validatePermissions(req, res, context);
//        } catch (e) {
//            return res.status(403).send();
//        }

        //TODO ????
//        if (!req.frontegg) {
//            req.frontegg = {};
//        }
//
//        req.frontegg.retryCount = 0;

        RequestContext context = ContextHolder.getRequestContext();
        logger.info("going to proxy request - " + request.getRequestURI() + " to  " + config.getUrlConfig().getBaseUrl());
        Map<String, String> proxyHeaders = initProxyHeaders(request, context.getFronteggContext());

        String url = config.getUrlConfig().getBaseUrl() + context.getRequestPath();
        Optional<Object> val = apiClient.service(url, request, response, proxyHeaders, Object.class);
        logger.info("Response  = " + val.get());
        return val.get();
    }

    private Map<String, String> initProxyHeaders(HttpServletRequest request, FronteggContext context) {
        String token = ContextHolder.getAuthentication().getAccessToken();
        String hostName = request.getLocalAddr();
        Map<String, String> headers = new HashMap<>();
        headers.put("x-access-token", token);
        headers.put("frontegg-tenant-id", context.getTenantId() == null  ? "" : context.getTenantId());
        headers.put("frontegg-user-id", context.getUserId() == null  ? "" : context.getUserId());
        headers.put("frontegg-vendor-host", hostName);
        return headers;
    }
}

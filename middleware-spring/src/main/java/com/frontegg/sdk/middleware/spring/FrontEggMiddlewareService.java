package com.frontegg.sdk.middleware.spring;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.common.exception.InvalidParameterException;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.AuthMiddleware;
import com.frontegg.sdk.middleware.FronteggOptions;
import com.frontegg.sdk.middleware.IFronteggMiddleware;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.IFronteggContextResolver;
import com.frontegg.sdk.middleware.spring.client.ApiClient;
import com.frontegg.sdk.middleware.spring.client.ApiClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FrontEggMiddlewareService implements IFronteggMiddleware {

    private static final Logger logger = LoggerFactory.getLogger(FrontEggMiddlewareService.class);

    private FronteggOptions fronteggOptions;
    private FronteggAuthenticator authenticator;
    private ApiClientFactory apiClientFactory;
    private FronteggConfig config;
    private FronteggConfigRoutsService fronteggConfigRoutsService;
    private AuthMiddleware authMiddleware;
    private IFronteggContextResolver contextResolver;

    private RestTemplate restTemplate = new RestTemplate();

    public FrontEggMiddlewareService(FronteggOptions fronteggOptions, FronteggConfig config) {
        this.fronteggOptions = fronteggOptions;
        this.config = config;
        this.authMiddleware = fronteggOptions.getAuthMiddleware();
        this.contextResolver = fronteggOptions.getContextResolver();
        validate(fronteggOptions);

        apiClientFactory = new ApiClientFactory(restTemplate);
        IApiClient apiClient = new ApiClient(restTemplate);
        authenticator = new FronteggAuthenticator(
                fronteggOptions.getClientId(),
                fronteggOptions.getApiKey(),
                config,
                apiClient
        );

        fronteggConfigRoutsService = new FronteggConfigRoutsService(apiClient, config);
    }


    private void validate(FronteggOptions options) {
        if (options == null) {
            throw new InvalidParameterException("Missing options");
        }

        if (StringHelper.isBlank(options.getClientId())) {
            throw new InvalidParameterException("Missing client ID");
        }

        if (StringHelper.isBlank(options.getApiKey())) {
            throw new InvalidParameterException("Missing api key");
        }
    }


    public void doProcess(HttpServletRequest request, HttpServletResponse response) {
        String method = request.getMethod();


        if (authMiddleware != null && !fronteggConfigRoutsService.isFronteggPublicRoute(request)) {
            logger.debug("will pass request threw the auth middleware");
            try {
                callMiddleware(request, response, authMiddleware);
                if (response.containsHeader("headersSent")) { //TODO ????
                    // response was already sent from the middleware, we have nothing left to do
                    return;
                }
            } catch (Exception ex) {
                logger.error("Failed to call middleware - ", ex);
                response.setStatus(401);
                //TODO build response

                return;
            }
        }

        FronteggContext context = contextResolver.resolveContext(request);

        if (method.equals("OPTIONS")) {
            response.setStatus(204);
            return;
        }

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

        proxyRequest(request, response, context);
    }

    private void callMiddleware(HttpServletRequest request, HttpServletResponse response, AuthMiddleware authMiddleware) {
        authMiddleware.callMiddleware(request, response);
    }

    /**
     * TODO enable retry mechanism
     * @param request
     * @param response
     * @param context
     */
    private void proxyRequest(HttpServletRequest request, HttpServletResponse response, FronteggContext context) {
        logger.info("going to proxy request - " + request.getRequestURI() + " to  " + config.getUrlConfig().getBaseUrl());
        IApiClient apiClient = new ApiClient(restTemplate);
        Map<String, String> proxyHeaders = initProxyHeaders(request, context);

        String url = config.getUrlConfig().getBaseUrl() + request.getRequestURI();
        Optional<Object> val = apiClient.service(url, request, response, proxyHeaders, Object.class);
        logger.info("Response  = " + val.get());
    }

    private Map<String, String> initProxyHeaders(HttpServletRequest request, FronteggContext context) {
        String hostName = request.getLocalAddr();
        Map<String, String> headers = new HashMap<>();
        headers.put("x-access-token", authenticator.getAccessToken());
        headers.put("frontegg-tenant-id", context.getTenantId() == null  ? "" : context.getTenantId());
        headers.put("frontegg-user-id", context.getUserId() == null  ? "" : context.getUserId());
        headers.put("frontegg-vendor-host", hostName);
        return headers;
    }
}

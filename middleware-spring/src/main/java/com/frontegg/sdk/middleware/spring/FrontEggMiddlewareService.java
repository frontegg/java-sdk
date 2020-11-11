package com.frontegg.sdk.middleware.spring;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.common.exception.InvalidParameterException;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.FronteggOptions;
import com.frontegg.sdk.middleware.IFronteggMiddleware;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.spring.client.ApiClient;
import com.frontegg.sdk.middleware.spring.client.ApiClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Optional;

public class FrontEggMiddlewareService implements IFronteggMiddleware {

    private static final Logger logger = LoggerFactory.getLogger(FrontEggMiddlewareService.class);

    private FronteggOptions fronteggOptions;
    private FronteggAuthenticator authenticator;
    private ApiClientFactory apiClientFactory;
    private FronteggConfig config;

    private RestTemplate restTemplate = new RestTemplate();

    public FrontEggMiddlewareService(FronteggOptions fronteggOptions, FronteggConfig config) {
        this.fronteggOptions = fronteggOptions;
        validate(fronteggOptions);
        apiClientFactory = new ApiClientFactory(restTemplate);
        this.config = config;
        IApiClient apiClient = new ApiClient(restTemplate);
        authenticator = new FronteggAuthenticator(fronteggOptions.getClientId(), fronteggOptions.getApiKey(), config, apiClient);
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

        //TODO ????
        FronteggContext context = new FronteggContext();
//        const context = await options.contextResolver(req);

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

    private void proxyRequest(HttpServletRequest request, HttpServletResponse response, FronteggContext context) {
        ApiClient apiClient = apiClientFactory.create(request.getRequestURI());
        HttpMethod method = HttpMethod.resolve(request.getMethod());

        authenticator.getAccessToken();

        switch (method) {
            case GET:
                Optional<Object> val = apiClient.get(Object.class, new HashMap<>());
                logger.info("Response  = " + val.get());
                break;
        }
    }

    /*
    headers: {
      'x-access-token': authenticator.accessToken,
      'frontegg-tenant-id': context && context.tenantId ? context.tenantId : '',
      'frontegg-user-id': context && context.userId ? context.userId : '',
      'frontegg-vendor-host': req.hostname,
    },
     */
}

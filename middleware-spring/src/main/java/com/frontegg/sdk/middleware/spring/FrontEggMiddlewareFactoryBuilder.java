package com.frontegg.sdk.middleware.spring;

import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.AuthMiddleware;
import com.frontegg.sdk.middleware.FronteggOptions;
import com.frontegg.sdk.middleware.IFronteggMiddleware;
import com.frontegg.sdk.middleware.context.IFronteggContextResolver;

public class FrontEggMiddlewareFactoryBuilder {

    private String clientID;
    private String apiKey;
    private boolean disableCors;
    private int maxRetries;
    private String cookieDomainRewrite;
    private FronteggConfig fronteggConfig;
    private AuthMiddleware authMiddleware;
    private IFronteggContextResolver contextResolver;

    public FrontEggMiddlewareFactoryBuilder withCredentials(String clientID, String apiKey) {
        this.apiKey = apiKey;
        this.clientID = clientID;
        return this;
    }

    public FrontEggMiddlewareFactoryBuilder disableCors(boolean disableCors) {
        this.disableCors = disableCors;
        return this;
    }

    public FrontEggMiddlewareFactoryBuilder maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public FrontEggMiddlewareFactoryBuilder cookieDomainRewrite(String cookieDomainRewrite) {
        this.cookieDomainRewrite = cookieDomainRewrite;
        return this;

    }

    public FrontEggMiddlewareFactoryBuilder withConfigs(FronteggConfig fronteggConfig) {
        this.fronteggConfig = fronteggConfig;
        return this;
    }

    public FrontEggMiddlewareFactoryBuilder withAuthMiddleware(AuthMiddleware authMiddleware) {
        this.authMiddleware = authMiddleware;
        return this;
    }

    public FrontEggMiddlewareFactoryBuilder withContextResolver(IFronteggContextResolver contextResolver) {
        this.contextResolver = contextResolver;
        return this;

    }

    public IFronteggMiddleware build() {
        FronteggOptions fronteggOptions = new FronteggOptions();
        fronteggOptions.setApiKey(apiKey);
        fronteggOptions.setClientId(clientID);
        fronteggOptions.setCookieDomainRewrite(cookieDomainRewrite);
        fronteggOptions.setDisableCors(disableCors);
        fronteggOptions.setMaxRetries(maxRetries);
        fronteggOptions.setAuthMiddleware(authMiddleware);
        fronteggOptions.setContextResolver(contextResolver);
        return new FrontEggMiddlewareService(fronteggOptions, fronteggConfig);
    }
}

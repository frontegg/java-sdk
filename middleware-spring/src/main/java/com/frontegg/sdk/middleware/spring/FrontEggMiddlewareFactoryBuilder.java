package com.frontegg.sdk.middleware.spring;

import com.frontegg.sdk.middleware.FronteggOptions;
import com.frontegg.sdk.middleware.IFronteggMiddleware;

public class FrontEggMiddlewareFactoryBuilder {

    private String clientID;
    private String apiKey;
    private boolean disableCors;
    private int maxRetries;
    private String cookieDomainRewrite;

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

    public IFronteggMiddleware build() {
        FronteggOptions fronteggOptions = new FronteggOptions();
        fronteggOptions.setApiKey(apiKey);
        fronteggOptions.setClientId(clientID);
        fronteggOptions.setCookieDomainRewrite(cookieDomainRewrite);
        fronteggOptions.setDisableCors(disableCors);
        fronteggOptions.setMaxRetries(maxRetries);
        return new FrontEggMiddlewareService(fronteggOptions);
    }
}

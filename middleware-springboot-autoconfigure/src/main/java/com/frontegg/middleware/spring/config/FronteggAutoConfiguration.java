package com.frontegg.middleware.spring.config;

import com.frontegg.sdk.middleware.FronteggOptions;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.FronteggContextResolver;
import com.frontegg.sdk.middleware.context.NoOpContextResolver;
import com.frontegg.sdk.middleware.routes.IFronteggRouteService;
import com.frontegg.sdk.middleware.spring.FronteggServiceDelegate;
import com.frontegg.sdk.middleware.spring.filter.FronteggFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@Configuration
@ComponentScan("com.frontegg.sdk.middleware.spring")
public class FronteggAutoConfiguration {

    @Value("${frontegg.clientId}")
    private String clientID;
    @Value("${frontegg.apiKey}")
    private String apiKey;
    @Value("${frontegg.basePath:/frontegg}")
    private String basePath;

    @Value("${frontegg.settings.disableCors:true}")
    private boolean disableCors;
    @Value("${frontegg.settings.maxRetries:3}")
    private int maxRetries;
    @Value("${frontegg.settings.cookieDomainRewrite:}")
    private String cookieDomainRewrite;

    @Bean
    @ConditionalOnMissingBean
    public FronteggContextResolver fronteggContextResolver() {
        return new NoOpContextResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public FronteggOptions fronteggOptions() {
        FronteggOptions fronteggOptions = new FronteggOptions();
        fronteggOptions.setMaxRetries(maxRetries);
        fronteggOptions.setDisableCors(disableCors);
        fronteggOptions.setCookieDomainRewrite(cookieDomainRewrite);
        fronteggOptions.setClientId(clientID);
        fronteggOptions.setApiKey(apiKey);
        return fronteggOptions;
    }

    @Bean
    @ConditionalOnMissingBean
    public FronteggFilter fronteggFilter(FronteggContextResolver fronteggContextResolver,
                                         FronteggAuthenticator authenticator,
                                         IFronteggRouteService fronteggRouteService,
                                         FronteggServiceDelegate fronteggServiceDelegate,
                                         FronteggOptions options) {
        Assert.notNull(fronteggContextResolver, "fronteggContextResolver cannot be null");
        Assert.notNull(fronteggRouteService, "fronteggRouteService cannot be null");
        Assert.notNull(fronteggServiceDelegate, "delegate cannot be null");
        Assert.notNull(options, "frontegg options cannot be null");

        return new FronteggFilter(basePath, authenticator, fronteggContextResolver, fronteggRouteService, fronteggServiceDelegate, options);
    }
}

package com.frontegg.sdk.middleware.spring.config;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.config.*;
import com.frontegg.sdk.middleware.FronteggOptions;
import com.frontegg.sdk.middleware.FronteggService;
import com.frontegg.sdk.middleware.IFronteggService;
import com.frontegg.sdk.middleware.authentication.IFronteggAuthenticationService;
import com.frontegg.sdk.middleware.authentication.impl.FronteggAuthenticationService;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.identity.IIdentityService;
import com.frontegg.sdk.middleware.identity.impl.IdentityService;
import com.frontegg.sdk.middleware.routes.IFronteggRouteService;
import com.frontegg.sdk.middleware.routes.impl.FronteggConfigRoutsService;
import com.frontegg.sdk.middleware.spring.FronteggListenerSupport;
import com.frontegg.sdk.middleware.spring.client.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;


@Configuration
public class FronteggConfiguration {

    @Autowired
    private SpringFronteggConfigProvider springFronteggConfigProvider;

    @Autowired
    private SpringWhiteConfigProvider springWhiteConfigProvider;

    @Autowired
    private FronteggOptions fronteggOptions;

    @Bean
    public ConfigProvider configProvider() {
        return new FronteggConfigProviderChain(
                springFronteggConfigProvider,
                new EnvironmentVariableConfigProvider(),
                new SystemPropertiesConfigProvider(),
                new DefaultConfigProvider()
        );
    }

    @Bean
    public WhiteListProvider whiteListProvider() {
        return new FronteggWhiteListProviderChain(
                springWhiteConfigProvider,
                new DefaultWhiteListProvider()
        );
    }

    @Bean
    public FronteggConfig fronteggConfig() {
        return configProvider().resolveConfigs();
    }

    @Bean
    public WhiteListConfig whiteListConfig() {
        return whiteListProvider().resolveConfigs();
    }

    @Bean
    public IApiClient apiClient() {
        return new ApiClient(new RestTemplate());
    }

    @Bean
    public FronteggAuthenticator fronteggAuthenticator() {
        return new FronteggAuthenticator(
                fronteggOptions.getClientId(),
                fronteggOptions.getApiKey(),
                fronteggConfig(),
                apiClient()
        );
    }

    @Bean
    public IFronteggAuthenticationService authenticationService() {
        return new FronteggAuthenticationService(fronteggAuthenticator(), identityService());
    }

    @Bean
    public IFronteggRouteService fronteggRouteService() {
        return new FronteggConfigRoutsService(apiClient(), fronteggConfig());
    }

    @Bean
    public IIdentityService identityService() {
        return new IdentityService(fronteggAuthenticator(), apiClient(), fronteggConfig());
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();


        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(1000L);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(fronteggOptions.getMaxRetries());
        retryTemplate.setRetryPolicy(retryPolicy);

        retryTemplate.registerListener(new FronteggListenerSupport());
        return retryTemplate;
    }

    @Bean
    public IFronteggService fronteggService(FronteggConfig config,
                                            IApiClient apiClient,
                                            FronteggAuthenticator authenticator,
                                            FronteggOptions fronteggOptions) {

        return new FronteggService(config, apiClient, authenticator, fronteggOptions);
    }

}

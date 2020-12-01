package com.frontegg.sdk.middleware.spring.config;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.config.*;
import com.frontegg.sdk.middleware.FronteggOptions;
import com.frontegg.sdk.middleware.FronteggServiceImpl;
import com.frontegg.sdk.middleware.FronteggService;
import com.frontegg.sdk.middleware.authentication.FronteggAuthenticationService;
import com.frontegg.sdk.middleware.authentication.impl.FronteggAuthenticationServiceImpl;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.identity.FronteggIdentityService;
import com.frontegg.sdk.middleware.identity.impl.FronteggIdentityServiceImpl;
import com.frontegg.sdk.middleware.routes.IFronteggRouteService;
import com.frontegg.sdk.middleware.routes.impl.FronteggConfigRoutsService;
import com.frontegg.sdk.middleware.spring.FronteggListenerSupport;
import com.frontegg.sdk.middleware.spring.client.SpringApiClient;
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
    public FronteggConfig fronteggConfig() {
        return configProvider().resolveConfigs();
    }

    @Bean
    public ApiClient apiClient() {
        return new SpringApiClient(new RestTemplate());
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
    public FronteggAuthenticationService authenticationService() {
        return new FronteggAuthenticationServiceImpl(fronteggAuthenticator(), fronteggIdentityService());
    }

    @Bean
    public IFronteggRouteService fronteggRouteService() {
        return new FronteggConfigRoutsService(apiClient(), fronteggConfig());
    }

    @Bean
    public FronteggIdentityService fronteggIdentityService() {
        return new FronteggIdentityServiceImpl(fronteggAuthenticator(), apiClient(), fronteggConfig());
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
    public FronteggService fronteggService(FronteggConfig config,
                                           ApiClient apiClient,
                                           FronteggAuthenticator authenticator,
                                           FronteggOptions fronteggOptions) {

        return new FronteggServiceImpl(config, apiClient, authenticator, fronteggOptions);
    }

}

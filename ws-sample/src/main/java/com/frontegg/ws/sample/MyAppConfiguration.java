package com.frontegg.ws.sample;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.config.*;
import com.frontegg.sdk.middleware.IPermissionEvaluator;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.context.IFronteggContextResolver;
import com.frontegg.sdk.middleware.spring.client.ApiClient;
import com.frontegg.sdk.middleware.spring.config.FronteggConfigProviderChain;
import com.frontegg.sdk.middleware.spring.config.SpringFronteggConfigProvider;
import com.frontegg.sdk.middleware.spring.context.DefaultFronteggContextResolver;
import com.frontegg.sdk.middleware.spring.service.impl.PermissionEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@ComponentScan("com.frontegg.sdk.middleware.spring")
@ComponentScan("com.frontegg.sdk.middleware.spring.config")
@Configuration
public class MyAppConfiguration {

    @Value("${frontegg.clientId}")
    private String clientID;
    @Value("${frontegg.apiKey}")
    private String apiKey;

    @Autowired
    private SpringFronteggConfigProvider springFronteggConfigProvider;

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
    public IFronteggContextResolver fronteggContextResolver() {
        return new DefaultFronteggContextResolver();
    }

    @Bean
    public FronteggConfig fronteggConfig() {
        return configProvider().resolveConfigs();
    }
    @Bean
    public IPermissionEvaluator permissionEvaluator() {
        return new PermissionEvaluator();
    }

    @Bean
    public IApiClient apiClient() {
        return new ApiClient(new RestTemplate());
    }

    @Bean
    public FronteggAuthenticator fronteggAuthenticator() {
        return new FronteggAuthenticator(clientID, apiKey, fronteggConfig(), apiClient());
    }
}

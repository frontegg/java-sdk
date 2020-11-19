package com.frontegg.ws.sample;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.audit.AuditClient;
import com.frontegg.sdk.audit.IAuditClient;
import com.frontegg.sdk.config.*;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.spring.client.ApiClient;
import com.frontegg.sdk.middleware.spring.config.FronteggConfigProviderChain;
import com.frontegg.sdk.middleware.spring.config.FronteggWhiteListProviderChain;
import com.frontegg.sdk.middleware.spring.config.SpringFronteggConfigProvider;
import com.frontegg.sdk.middleware.spring.config.SpringWhiteConfigProvider;
import com.frontegg.sdk.middleware.spring.filter.FronteggContextFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.servlet.Filter;

@ComponentScan("com.frontegg.sdk.middleware.spring")
@Configuration
public class MyAppConfiguration {

    @Value("${frontegg.clientId}")
    private String clientID;
    @Value("${frontegg.apiKey}")
    private String apiKey;

    @Autowired
    private SpringFronteggConfigProvider springFronteggConfigProvider;
    @Autowired
    private SpringWhiteConfigProvider springWhiteConfigProvider;

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
    public Filter frontEggFilter() {
        return new FronteggContextFilter();
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
        return new FronteggAuthenticator(clientID, apiKey, fronteggConfig(), apiClient());
    }

    @Bean
    public IAuditClient auditClient() {
        return new AuditClient(fronteggAuthenticator(), apiClient(), fronteggConfig());
    }
}

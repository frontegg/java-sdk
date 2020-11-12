package com.frontegg.ws.sample;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.config.*;
import com.frontegg.sdk.middleware.context.IFronteggContextResolver;
import com.frontegg.sdk.middleware.spring.client.ApiClient;
import com.frontegg.sdk.middleware.spring.config.FronteggConfigProviderChain;
import com.frontegg.sdk.middleware.spring.config.SpringFronteggConfigProvider;
import com.frontegg.sdk.middleware.spring.context.DefaultFronteggContextResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@ComponentScan("com.frontegg.sdk.middleware.spring")
@ComponentScan("com.frontegg.sdk.middleware.spring.config")
@Configuration
public class MyAppConfiguration {

    @Bean
    public ConfigProvider configProvider(SpringFronteggConfigProvider springFronteggConfigProvider) {
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
    public FronteggConfig fronteggConfig(SpringFronteggConfigProvider springFronteggConfigProvider) {
        return configProvider(springFronteggConfigProvider).resolveConfigs();
    }

    @Bean
    public IApiClient apiClient() {
        return new ApiClient(new RestTemplate());
    }

}

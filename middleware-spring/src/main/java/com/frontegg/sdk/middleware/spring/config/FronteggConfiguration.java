package com.frontegg.sdk.middleware.spring.config;

import com.frontegg.sdk.config.ConfigProvider;
import com.frontegg.sdk.config.DefaultConfigProvider;
import com.frontegg.sdk.config.EnvironmentVariableConfigProvider;
import com.frontegg.sdk.config.SystemPropertiesConfigProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FronteggConfiguration {

    @Autowired
    private SpringFronteggConfigProvider springFronteggConfigProvider;


    @Bean
    public ConfigProvider configProvider() {
        return new FronteggConfigProviderChain(
                new EnvironmentVariableConfigProvider(),
                new SystemPropertiesConfigProvider(),
                new DefaultConfigProvider(),
                springFronteggConfigProvider
        );
    }
}

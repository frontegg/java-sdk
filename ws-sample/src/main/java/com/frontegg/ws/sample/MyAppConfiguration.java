package com.frontegg.ws.sample;

import com.frontegg.sdk.config.ConfigProvider;
import com.frontegg.sdk.config.DefaultConfigProvider;
import com.frontegg.sdk.config.EnvironmentVariableConfigProvider;
import com.frontegg.sdk.config.SystemPropertiesConfigProvider;
import com.frontegg.sdk.middleware.AuthMiddleware;
import com.frontegg.sdk.middleware.IFronteggMiddleware;
import com.frontegg.sdk.middleware.context.IFronteggContextResolver;
import com.frontegg.sdk.middleware.spring.FrontEggMiddlewareFactoryBuilder;
import com.frontegg.sdk.middleware.spring.config.FronteggConfigProviderChain;
import com.frontegg.sdk.middleware.spring.config.SpringFronteggConfigProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan("com.frontegg.sdk.middleware.spring.config")
@Configuration
public class MyAppConfiguration {

    @Value("${frontegg.clientId}")
    private String clientID;
    @Value("${frontegg.apiKey}")
    private String apiKey;

    @Value("${frontegg.settings.disableCors:#{true}}")
    private boolean disableCors;
    @Value("${frontegg.settings.maxRetries:#{3}}")
    private int maxRetries;
    @Value("${frontegg.settings.cookieDomainRewrite:#{''}}")
    private String cookieDomainRewrite;


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
    public IFronteggMiddleware fronteggMiddleware(SpringFronteggConfigProvider springFronteggConfigProvider) {
        return new FrontEggMiddlewareFactoryBuilder()
                .withCredentials(clientID, apiKey)
                .withAuthMiddleware(authMiddleware())
                .withContextResolver(contextResolver())
                .disableCors(disableCors)
                .maxRetries(maxRetries)
                .cookieDomainRewrite(cookieDomainRewrite)
                .withConfigs(configProvider(springFronteggConfigProvider).resolveConfigs())
                .build();
    }

    @Bean
    public AuthMiddleware authMiddleware() {
        return new MyCustomeMiddleware();
    }

    //TODO data should be populated after JWT verification
    @Bean
    public IFronteggContextResolver contextResolver() {
        return new FronteggContextResolver();
    }


}

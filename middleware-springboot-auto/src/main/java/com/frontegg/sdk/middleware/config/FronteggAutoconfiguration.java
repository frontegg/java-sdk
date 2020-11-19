package com.frontegg.sdk.middleware.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.frontegg.sdk")
public class FronteggAutoconfiguration {

    @Value("${frontegg.clientId}")
    private String clientID;
    @Value("${frontegg.apiKey}")
    private String apiKey;

    @Value("${frontegg.settings.disableCors#{true}}")
    private boolean disableCors;
    @Value("${frontegg.settings.maxRetries#{3}}")
    private int maxRetries;
    @Value("${frontegg.settings.cookieDomainRewrite#{''}}")
    private String cookieDomainRewrite;
}

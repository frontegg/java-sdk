package com.frontegg.ws.sample;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.audit.AuditClient;
import com.frontegg.sdk.audit.IAuditClient;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.frontegg.sdk.middleware.spring")
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public IAuditClient auditClient(FronteggAuthenticator authenticator, IApiClient apiClient, FronteggConfig config) {
        return new AuditClient(authenticator, apiClient, config);
    }

}

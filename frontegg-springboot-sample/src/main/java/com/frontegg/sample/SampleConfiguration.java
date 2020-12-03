package com.frontegg.sample;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.audit.AuditClient;
import com.frontegg.sdk.audit.impl.AuditClientImpl;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleConfiguration {

    @Bean
    public AuditClient eventsClient(FronteggAuthenticator fronteggAuthenticator,
                                    ApiClient apiClient,
                                    FronteggConfig config) {
        return new AuditClientImpl(fronteggAuthenticator, apiClient, config);
    }
}

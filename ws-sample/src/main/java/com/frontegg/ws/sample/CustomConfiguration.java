package com.frontegg.ws.sample;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.audit.AuditClient;
import com.frontegg.sdk.audit.IAuditClient;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.FronteggOptions;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.spring.core.EnableFrontegg;
import com.frontegg.sdk.middleware.spring.core.FronteggConfigurerAdapter;
import com.frontegg.sdk.middleware.spring.core.builders.Frontegg;
import com.frontegg.sdk.sso.ISsoClient;
import com.frontegg.sdk.sso.SsoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableFrontegg
@Configuration
public class CustomConfiguration extends FronteggConfigurerAdapter {

    @Value("${frontegg.clientId}")
    private String clientID;
    @Value("${frontegg.apiKey}")
    private String apiKey;

    @Value("${frontegg.settings.disableCors:true}")
    private boolean disableCors;
    @Value("${frontegg.settings.maxRetries:3}")
    private int maxRetries;
    @Value("${frontegg.settings.cookieDomainRewrite:}")
    private String cookieDomainRewrite;


    @Override
    protected void configure(Frontegg frontegg) throws Exception {
        if (disableCors) {
            frontegg.cors().disable();
        } else {
            frontegg.cors().configurationSource(corsConfigurationSource());
        }
    }

    @Override
    protected String getPath() {
        return "/frontegg";
    }

    @Bean
    public IAuditClient auditClient(FronteggAuthenticator authenticator, ApiClient apiClient, FronteggConfig config) {
        return new AuditClient(authenticator, apiClient, config);
    }

    @Bean
    public ISsoClient ssoClient(FronteggAuthenticator authenticator, ApiClient apiClient, FronteggConfig config) {
        return new SsoClient(authenticator, apiClient, config);
    }

    @Bean
    public FronteggOptions fronteggOptions() {
        FronteggOptions fronteggOptions = new FronteggOptions();
        fronteggOptions.setMaxRetries(maxRetries);
        fronteggOptions.setDisableCors(disableCors);
        fronteggOptions.setCookieDomainRewrite(cookieDomainRewrite);
        fronteggOptions.setClientId(clientID);
        fronteggOptions.setApiKey(apiKey);
        return fronteggOptions;
    }

    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3001"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "PATCH", "OPTION"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "x-frontegg-source"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
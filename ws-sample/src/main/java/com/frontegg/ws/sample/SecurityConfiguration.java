package com.frontegg.ws.sample;

import com.frontegg.sdk.middleware.FronteggOptions;
import com.frontegg.sdk.middleware.authentication.FronteggAuthenticationService;
import com.frontegg.sdk.middleware.routes.IFronteggRouteService;
import com.frontegg.sdk.middleware.spring.FronteggServiceDelegate;
import com.frontegg.sdk.middleware.spring.filter.FronteggFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@ComponentScan("com.frontegg.sdk.middleware.spring")
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${frontegg.clientId}")
    private String clientID;
    @Value("${frontegg.apiKey}")
    private String apiKey;
    @Value("${frontegg.basePath:/frontegg}")
    private String basePath;

    @Value("${frontegg.settings.disableCors:true}")
    private boolean disableCors;
    @Value("${frontegg.settings.maxRetries:3}")
    private int maxRetries;
    @Value("${frontegg.settings.cookieDomainRewrite:}")
    private String cookieDomainRewrite;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable().
             cors().configurationSource(corsConfigurationSource()).
                and()
                .authorizeRequests().antMatchers(basePath + "/**").permitAll()
                .anyRequest().authenticated();

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

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public FronteggFilter fronteggFilter(FronteggAuthenticationService authenticationService,
                                        IFronteggRouteService fronteggRouteService,
                                        FronteggServiceDelegate fronteggServiceDelegate,
                                        FronteggOptions options) {
        Assert.notNull(authenticationService, "authenticationService cannot be null");
        Assert.notNull(fronteggRouteService, "fronteggRouteService cannot be null");
        Assert.notNull(fronteggServiceDelegate, "delegate cannot be null");
        Assert.notNull(options, "frontegg options cannot be null");

        return new FronteggFilter(basePath, authenticationService, fronteggRouteService, fronteggServiceDelegate, options);
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

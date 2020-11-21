package com.frontegg.ws.sample;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.audit.AuditClient;
import com.frontegg.sdk.audit.IAuditClient;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authentication.IFronteggAuthenticationService;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import com.frontegg.sdk.middleware.routes.IFronteggRouteService;
import com.frontegg.sdk.middleware.spring.filter.FronteggAuthenticationFilter;
import com.frontegg.sdk.middleware.spring.filter.FronteggContextFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;

import javax.servlet.Filter;

@ComponentScan({
        "com.frontegg.sdk.middleware.spring",
        "com.frontegg.ws"
})
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public IAuditClient auditClient(FronteggAuthenticator authenticator, IApiClient apiClient, FronteggConfig config) {
        return new AuditClient(authenticator, apiClient, config);
    }

    @Bean
    public FilterRegistrationBean<Filter> fronteggFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setName("fronteggFilter");
        registrationBean.setOrder(1);
        registrationBean.setFilter(new FronteggContextFilter());
        registrationBean.addUrlPatterns("/frontegg/*");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> fronteggAuthenticationFilter(IFronteggAuthenticationService authenticationService,
                                               IFronteggRouteService fronteggRouteService) {

        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setName("fronteggAuthenticationFilter");
        registrationBean.setOrder(2);
        registrationBean.setFilter(new FronteggAuthenticationFilter(authenticationService, fronteggRouteService));
        registrationBean.addUrlPatterns("/frontegg/*");
        return registrationBean;
    }

}

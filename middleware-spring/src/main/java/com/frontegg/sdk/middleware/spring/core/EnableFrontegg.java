package com.frontegg.sdk.middleware.spring.core;

import com.frontegg.sdk.middleware.spring.config.FronteggConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = { java.lang.annotation.ElementType.TYPE })
@Import({ FronteggConfiguration.class, FronteggConfigurations.class})
@Configuration
public @interface EnableFrontegg {

}

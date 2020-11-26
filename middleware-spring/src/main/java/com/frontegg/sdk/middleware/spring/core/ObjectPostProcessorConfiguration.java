package com.frontegg.sdk.middleware.spring.core;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Lazy
@Configuration
public class ObjectPostProcessorConfiguration {

    @Bean
    public ObjectPostProcessor<Object> objectPostProcessor(
            AutowireCapableBeanFactory beanFactory) {
        return new AutowireBeanFactoryObjectPostProcessor(beanFactory);
    }
}

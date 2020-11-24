package com.frontegg.sdk.middleware.spring.core;

import com.frontegg.sdk.middleware.spring.core.builders.FronteggConf;
import com.frontegg.sdk.middleware.spring.core.configurers.FronteggConfigurer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class AutowiredFronteggConfigurersIgnoreParents {

    private final ConfigurableListableBeanFactory beanFactory;

    AutowiredFronteggConfigurersIgnoreParents(
            ConfigurableListableBeanFactory beanFactory) {
        Assert.notNull(beanFactory, "beanFactory cannot be null");
        this.beanFactory = beanFactory;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<FronteggConfigurer<Filter, FronteggConf>> getFronteggConfigurers() {
        List<FronteggConfigurer<Filter, FronteggConf>> fronteggConfigurers = new ArrayList<>();
        Map<String, FronteggConfigurer> beansOfType = beanFactory
                .getBeansOfType(FronteggConfigurer.class);
        for (Map.Entry<String, FronteggConfigurer> entry : beansOfType.entrySet()) {
            fronteggConfigurers.add(entry.getValue());
        }
        return fronteggConfigurers;
    }
}

package com.frontegg.sdk.middleware.config;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
public class FronteggConfiguration implements ImportAware, BeanClassLoaderAware {

    private ClassLoader beanClassLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void setImportMetadata(AnnotationMetadata annotationMetadata) {

    }
}

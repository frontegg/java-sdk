package com.frontegg.sdk.middleware.spring.core;


import java.util.ArrayList;
import java.util.List;

import com.frontegg.sdk.middleware.spring.core.ObjectPostProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

/**
 * Allows registering Objects to participate with an {@link AutowireCapableBeanFactory}'s
 * post processing of {@link Aware} methods, {@link InitializingBean#afterPropertiesSet()}
 * , and {@link DisposableBean#destroy()}.
 */
final class AutowireBeanFactoryObjectPostProcessor
        implements ObjectPostProcessor<Object>, DisposableBean, SmartInitializingSingleton {
    private final Log logger = LogFactory.getLog(getClass());
    private final AutowireCapableBeanFactory autowireBeanFactory;
    private final List<DisposableBean> disposableBeans = new ArrayList<>();
    private final List<SmartInitializingSingleton> smartSingletons = new ArrayList<>();

    AutowireBeanFactoryObjectPostProcessor(
            AutowireCapableBeanFactory autowireBeanFactory) {
        Assert.notNull(autowireBeanFactory, "autowireBeanFactory cannot be null");
        this.autowireBeanFactory = autowireBeanFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> T postProcess(T object) {
        if (object == null) {
            return null;
        }
        T result = null;
        try {
            result = (T) this.autowireBeanFactory.initializeBean(object,  object.toString());
        }
        catch (RuntimeException e) {
            Class<?> type = object.getClass();
            throw new RuntimeException( "Could not postProcess " + object + " of type " + type, e);
        }

        this.autowireBeanFactory.autowireBean(object);

        if (result instanceof DisposableBean) {
            this.disposableBeans.add((DisposableBean) result);
        }

        if (result instanceof SmartInitializingSingleton) {
            this.smartSingletons.add((SmartInitializingSingleton) result);
        }
        return result;
    }

    @Override
    public void afterSingletonsInstantiated() {
        for (SmartInitializingSingleton singleton : smartSingletons) {
            singleton.afterSingletonsInstantiated();
        }
    }

    public void destroy() {
        for (DisposableBean disposable : this.disposableBeans) {
            try {
                disposable.destroy();
            }
            catch (Exception error) {
                this.logger.error(error);
            }
        }
    }

}

package com.frontegg.sdk.middleware.spring.core;

import com.frontegg.sdk.middleware.spring.core.builders.Frontegg;
import com.frontegg.sdk.middleware.spring.core.builders.FronteggConf;
import com.frontegg.sdk.middleware.spring.core.configurers.FronteggConfigurer;
import com.frontegg.sdk.middleware.spring.filter.FronteggFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;


public abstract class FronteggConfigurerAdapter implements FronteggConfigurer<Filter, FronteggConf> {
    static final String DEFAULT_FRONTEGG_BASE_URL = "/frontegg";
    private ApplicationContext context;

    private Frontegg frontegg;

    private ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<Object>() {
        public <T> T postProcess(T object) {
            throw new IllegalStateException(
                    ObjectPostProcessor.class.getName()
                            + " is a required bean. Ensure you have used @EnableFrontegg and @Configuration");
        }
    };

    protected FronteggConfigurerAdapter() {}


    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected final Frontegg getFrontegg() throws Exception {
        if (frontegg != null) {
            return frontegg;
        }

        Map<Class<?>, Object> sharedObjects = createSharedObjects();
        frontegg = new Frontegg(objectPostProcessor, sharedObjects);
        frontegg.fronteggFilters()
                    .filter(getPath(), context.getBean(FronteggFilter.class));
        configure(frontegg);
        return frontegg;
    }

    private Map<Class<?>, Object> createSharedObjects() {
        Map<Class<?>, Object> sharedObjects = new HashMap<>();
        sharedObjects.put(ApplicationContext.class, context);
        return sharedObjects;
    }

    @Override
    public void init(FronteggConf fronteggConf) throws Exception {
        final Frontegg frontegg = getFrontegg();
        fronteggConf.addFronteggFilterChainBuilder(frontegg).postBuildAction(() -> {});
    }

    @Override
    public void configure(FronteggConf builder) throws Exception {

    }

    protected void configure(Frontegg frontegg) throws Exception {

    }

    protected final ApplicationContext getApplicationContext() {
        return this.context;
    }

    protected String getPath() {
        return DEFAULT_FRONTEGG_BASE_URL;
    }

    @Autowired
    public void setObjectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor) {
        this.objectPostProcessor = objectPostProcessor;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }


}

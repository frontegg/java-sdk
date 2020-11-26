package com.frontegg.sdk.middleware.spring.core.builders;

import com.frontegg.sdk.middleware.spring.core.FilterChainProxy;
import com.frontegg.sdk.middleware.spring.core.FronteggFilterChain;
import com.frontegg.sdk.middleware.spring.core.ObjectPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

public final class FronteggConf extends
        AbstractConfiguredFronteggBuilder<Filter, FronteggConf> implements
        FronteggBuilder<Filter>,
        ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final List<FronteggBuilder<? extends FronteggFilterChain>> fronteggFilterChainBuilders = new ArrayList<>();


    private Runnable postBuildAction = () -> {
    };

    public FronteggConf(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
       this.applicationContext = applicationContext;
    }


    public FronteggConf postBuildAction(Runnable postBuildAction) {
        this.postBuildAction = postBuildAction;
        return this;
    }

    @Override
    protected Filter performBuild() throws Exception {
        Assert.state(
                !fronteggFilterChainBuilders.isEmpty(),
                "At least one FronteggBuilder<? extends FronteggFilterChain> needs to be specified. "
                        + "Typically this done by adding a @Configuration that extends FronteggConfigurerAdapter. "
                        + "More advanced users can invoke "
                        + FronteggConf.class.getSimpleName()
                        + ".addFronteggFilterChainBuilder directly");

        int chainSize = fronteggFilterChainBuilders.size();
        List<FronteggFilterChain> fronteggFilterChains = new ArrayList<>(chainSize);



        for (FronteggBuilder<? extends FronteggFilterChain> fronteggFilterChainBuilder : fronteggFilterChainBuilders) {
            fronteggFilterChains.add(fronteggFilterChainBuilder.build());
        }

        FilterChainProxy filterChainProxy = new FilterChainProxy(fronteggFilterChains);

        filterChainProxy.afterPropertiesSet();

        Filter result = filterChainProxy;
        postBuildAction.run();
        return result;
    }

    public FronteggConf addFronteggFilterChainBuilder(FronteggBuilder<? extends FronteggFilterChain> fronteggFilterChainBuilder) {
        this.fronteggFilterChainBuilders.add(fronteggFilterChainBuilder);
        return this;
    }
}

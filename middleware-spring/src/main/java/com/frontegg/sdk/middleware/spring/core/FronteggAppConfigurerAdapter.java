package com.frontegg.sdk.middleware.spring.core;

import com.frontegg.sdk.middleware.spring.core.builders.FronteggBuilder;
import com.frontegg.sdk.middleware.spring.core.configurers.FronteggConfigurer;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.ArrayList;
import java.util.List;

public class FronteggAppConfigurerAdapter<O, B extends FronteggBuilder<O>> implements FronteggConfigurer<O, B> {
    private B fronteggBuilder;

    private CompositeObjectPostProcessor objectPostProcessor = new CompositeObjectPostProcessor();

    public void init(B builder) throws Exception {
    }

    public void configure(B builder) throws Exception {
    }

    public B and() {
        return getBuilder();
    }

    protected final B getBuilder() {
        if (fronteggBuilder == null) {
            throw new IllegalStateException("fronteggBuilder cannot be null");
        }
        return fronteggBuilder;
    }

    @SuppressWarnings("unchecked")
    protected <T> T postProcess(T object) {
        return (T) this.objectPostProcessor.postProcess(object);
    }

    public void addObjectPostProcessor(ObjectPostProcessor<?> objectPostProcessor) {
        this.objectPostProcessor.addObjectPostProcessor(objectPostProcessor);
    }

    public void setBuilder(B builder) {
        this.fronteggBuilder = builder;
    }

    private static final class CompositeObjectPostProcessor implements
            ObjectPostProcessor<Object> {
        private List<ObjectPostProcessor<?>> postProcessors = new ArrayList<>();

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public Object postProcess(Object object) {
            for (ObjectPostProcessor opp : postProcessors) {
                Class<?> oppClass = opp.getClass();
                Class<?> oppType = GenericTypeResolver.resolveTypeArgument(oppClass, ObjectPostProcessor.class);
                if (oppType == null || oppType.isAssignableFrom(object.getClass())) {
                    object = opp.postProcess(object);
                }
            }
            return object;
        }

        private boolean addObjectPostProcessor(ObjectPostProcessor<?> objectPostProcessor) {
            boolean result = this.postProcessors.add(objectPostProcessor);
            postProcessors.sort(AnnotationAwareOrderComparator.INSTANCE);
            return result;
        }
    }
}

package com.frontegg.sdk.middleware.spring.core.builders;

import com.frontegg.sdk.middleware.spring.core.FronteggAppConfigurerAdapter;
import com.frontegg.sdk.middleware.spring.core.ObjectPostProcessor;
import com.frontegg.sdk.middleware.spring.core.configurers.FronteggConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.*;

public abstract class AbstractConfiguredFronteggBuilder <O, B extends FronteggBuilder<O>> extends AbstractFronteggBuilder<O> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LinkedHashMap<Class<? extends FronteggConfigurer<O, B>>, List<FronteggConfigurer<O, B>>> configurers = new LinkedHashMap<>();
    private final List<FronteggConfigurer<O, B>> configurersAddedInInitializing = new ArrayList<>();
    private BuildState buildState = BuildState.UNBUILT;

    private ObjectPostProcessor<Object> objectPostProcessor;
    private final boolean allowConfigurersOfSameType;
    private final Map<Class<?>, Object> sharedObjects = new HashMap<>();

    protected AbstractConfiguredFronteggBuilder(ObjectPostProcessor<Object> objectPostProcessor) {
        this(objectPostProcessor, false);
    }

    protected AbstractConfiguredFronteggBuilder(
            ObjectPostProcessor<Object> objectPostProcessor,
            boolean allowConfigurersOfSameType) {
        Assert.notNull(objectPostProcessor, "objectPostProcessor cannot be null");
        this.objectPostProcessor = objectPostProcessor;
        this.allowConfigurersOfSameType = allowConfigurersOfSameType;
    }

    public <C extends FronteggConfigurer<O, B>> C apply(C configurer) throws Exception {
        add(configurer);
        return configurer;
    }

    public <C extends FronteggAppConfigurerAdapter<O, B>> C apply(C configurer) throws Exception {
        configurer.addObjectPostProcessor(objectPostProcessor);
        configurer.setBuilder((B) this);
        add(configurer);
        return configurer;
    }


    /**
     * Adds {@link FronteggConfigurer} ensuring that it is allowed and invoking
     * {@link FronteggConfigurer#init(FronteggBuilder)} immediately if necessary.
     *
     * @param configurer the {@link FronteggConfigurer} to add
     */
    @SuppressWarnings("unchecked")
    private <C extends FronteggConfigurer<O, B>> void add(C configurer) {
        Assert.notNull(configurer, "configurer cannot be null");

        Class<? extends FronteggConfigurer<O, B>> clazz = (Class<? extends FronteggConfigurer<O, B>>) configurer
                .getClass();
        synchronized (configurers) {
            if (buildState.isConfigured()) {
                throw new IllegalStateException("Cannot apply " + configurer
                        + " to already built object");
            }
            List<FronteggConfigurer<O, B>> configs = allowConfigurersOfSameType ? this.configurers
                    .get(clazz) : null;
            if (configs == null) {
                configs = new ArrayList<>(1);
            }
            configs.add(configurer);
            this.configurers.put(clazz, configs);
            if (buildState.isInitializing()) {
                this.configurersAddedInInitializing.add(configurer);
            }
        }
    }

    /**
     * Subclasses must implement this method to build the object that is being returned.
     *
     * @return the Object to be buit or null if the implementation allows it
     */
    protected abstract O performBuild() throws Exception;


    @Override
    protected final O doBuild() throws Exception {
        synchronized (configurers) {
            buildState = BuildState.INITIALIZING;

            beforeInit();
            init();

            buildState = BuildState.CONFIGURING;

            beforeConfigure();
            configure();

            buildState = BuildState.BUILDING;

            O result = performBuild();

            buildState = BuildState.BUILT;

            return result;
        }
    }

    protected void beforeInit() throws Exception {
    }

    protected void beforeConfigure() throws Exception {
    }

    private void init() throws Exception {
        Collection<FronteggConfigurer<O, B>> configurers = getConfigurers();

        for (FronteggConfigurer<O, B> configurer : configurers) {
            configurer.init((B) this);
        }

        for (FronteggConfigurer<O, B> configurer : configurersAddedInInitializing) {
            configurer.init((B) this);
        }
    }

    private void configure() throws Exception {
        Collection<FronteggConfigurer<O, B>> configurers = getConfigurers();

        for (FronteggConfigurer<O, B> configurer : configurers) {
            configurer.configure((B) this);
        }
    }

    private Collection<FronteggConfigurer<O, B>> getConfigurers() {
        List<FronteggConfigurer<O, B>> result = new ArrayList<>();
        for (List<FronteggConfigurer<O, B>> configs : this.configurers.values()) {
            result.addAll(configs);
        }
        return result;
    }

    public <C extends FronteggConfigurer<O, B>> C getConfigurer(Class<C> clazz) {
        List<FronteggConfigurer<O, B>> configs = this.configurers.get(clazz);
        if (configs == null) {
            return null;
        }
        if (configs.size() != 1) {
            throw new IllegalStateException("Only one configurer expected for type "
                    + clazz + ", but got " + configs);
        }
        return (C) configs.get(0);
    }

    public <C extends FronteggConfigurer<O, B>> C removeConfigurer(Class<C> clazz) {
        List<FronteggConfigurer<O, B>> configs = this.configurers.remove(clazz);
        if (configs == null) {
            return null;
        }
        if (configs.size() != 1) {
            throw new IllegalStateException("Only one configurer expected for type "
                    + clazz + ", but got " + configs);
        }
        return (C) configs.get(0);
    }

    @SuppressWarnings("unchecked")
    public <C> void setSharedObject(Class<C> sharedType, C object) {
        this.sharedObjects.put(sharedType, object);
    }

    @SuppressWarnings("unchecked")
    public <C> C getSharedObject(Class<C> sharedType) {
        return (C) this.sharedObjects.get(sharedType);
    }

    public Map<Class<?>, Object> getSharedObjects() {
        return Collections.unmodifiableMap(this.sharedObjects);
    }

    /**
     * The build state for the application
     */
    private enum BuildState {
        /**
         * This is the state before the {@link FronteggBuilder#build()} is invoked
         */
        UNBUILT(0),

        /**
         * The state from when {@link FronteggBuilder#build()} is first invoked until all the
         * {@link FronteggConfigurer#init(FronteggBuilder)} methods have been invoked.
         */
        INITIALIZING(1),

        /**
         * The state from after all {@link FronteggConfigurer#init(FronteggBuilder)} have
         * been invoked until after all the
         * {@link FronteggConfigurer#configure(FronteggBuilder)} methods have been
         * invoked.
         */
        CONFIGURING(2),

        /**
         * From the point after all the
         * {@link FronteggConfigurer#configure(FronteggBuilder)} have completed to just
         * after {@link AbstractConfiguredFronteggBuilder#performBuild()}.
         */
        BUILDING(3),

        /**
         * After the object has been completely built.
         */
        BUILT(4);

        private final int order;

        BuildState(int order) {
            this.order = order;
        }

        public boolean isInitializing() {
            return INITIALIZING.order == order;
        }

        /**
         * Determines if the state is CONFIGURING or later
         * @return
         */
        public boolean isConfigured() {
            return order >= CONFIGURING.order;
        }
    }
}

package com.frontegg.sdk.middleware.spring.core;

import com.frontegg.sdk.middleware.FronteggOptions;
import com.frontegg.sdk.middleware.authentication.FronteggAuthenticationService;
import com.frontegg.sdk.middleware.routes.IFronteggRouteService;
import com.frontegg.sdk.middleware.spring.FronteggServiceDelegate;
import com.frontegg.sdk.middleware.spring.core.builders.FronteggConf;
import com.frontegg.sdk.middleware.spring.core.configurers.FronteggConfigurer;
import com.frontegg.sdk.middleware.spring.filter.FronteggFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;

import javax.servlet.Filter;
import java.util.List;

@ComponentScan("com.frontegg.sdk.middleware.spring")
@Configuration
public class FronteggConfigurations {

    private FronteggConf fronteggConf;
    private List<FronteggConfigurer<Filter, FronteggConf>> fronteggConfigurers;

    @Autowired(required = false)
    private ObjectPostProcessor<Object> objectObjectPostProcessor;

    @Bean(name = AbstractFronteggWebApplicationInitializer.DEFAULT_FILTER_NAME)
    public Filter fronteggAppFilterChain() throws Exception {
        boolean hasConfigurers = fronteggConfigurers != null
                && !fronteggConfigurers.isEmpty();
        if (!hasConfigurers) {
            FronteggConfigurerAdapter adapter = objectObjectPostProcessor
                    .postProcess(new FronteggConfigurerAdapter() {

                    });
            fronteggConf.apply(adapter);
        }
        return fronteggConf.build();
    }

    @Autowired(required = false)
    public void setFilterChainProxyFronteggConfigurer(
            ObjectPostProcessor<Object> objectPostProcessor,
            @Value("#{@autowiredFronteggConfigurersIgnoreParents.getFronteggConfigurers()}") List<FronteggConfigurer<Filter, FronteggConf>> fronteggConfigurers)
            throws Exception {
        fronteggConf = objectPostProcessor
                .postProcess(new FronteggConf(objectPostProcessor));

        fronteggConfigurers.sort(AnnotationAwareOrderComparator.INSTANCE);

        Integer previousOrder = null;
        Object previousConfig = null;
        for (FronteggConfigurer<Filter, FronteggConf> config : fronteggConfigurers) {
            Integer order = AnnotationAwareOrderComparator.lookupOrder(config);
            if (previousOrder != null && previousOrder.equals(order)) {
                throw new IllegalStateException(
                        "@Order on FronteggConfigurers must be unique. Order of "
                                + order + " was already used on " + previousConfig + ", so it cannot be used on "
                                + config + " too.");
            }
            previousOrder = order;
            previousConfig = config;
        }

        for (FronteggConfigurer<Filter, FronteggConf> fronteggConfigurer : fronteggConfigurers) {
            fronteggConf.apply(fronteggConfigurer);
        }

        this.fronteggConfigurers = fronteggConfigurers;
    }

    @Bean
    public static AutowiredFronteggConfigurersIgnoreParents autowiredFronteggConfigurersIgnoreParents(
            ConfigurableListableBeanFactory beanFactory) {
        return new AutowiredFronteggConfigurersIgnoreParents(beanFactory);
    }


    private static class AnnotationAwareOrderComparator extends OrderComparator {
        private static final AnnotationAwareOrderComparator INSTANCE = new AnnotationAwareOrderComparator();

        @Override
        protected int getOrder(Object obj) {
            return lookupOrder(obj);
        }

        private static int lookupOrder(Object obj) {
            if (obj instanceof Ordered) {
                return ((Ordered) obj).getOrder();
            }
            if (obj != null) {
                Class<?> clazz = (obj instanceof Class ? (Class<?>) obj : obj.getClass());
                Order order = AnnotationUtils.findAnnotation(clazz, Order.class);
                if (order != null) {
                    return order.value();
                }
            }
            return Ordered.LOWEST_PRECEDENCE;
        }
    }

    @Bean
    public FronteggFilter fronteggAppFilter(FronteggAuthenticationService authenticationService,
                                            IFronteggRouteService fronteggRouteService,
                                            FronteggServiceDelegate fronteggServiceDelegate,
                                            FronteggOptions options) {
        Assert.notNull(authenticationService, "authenticationService cannot be null");
        Assert.notNull(fronteggRouteService, "fronteggRouteService cannot be null");
        Assert.notNull(fronteggServiceDelegate, "delegate cannot be null");
        Assert.notNull(options, "frontegg options cannot be null");

        return new FronteggFilter(authenticationService, fronteggRouteService, fronteggServiceDelegate, options);
    }
}

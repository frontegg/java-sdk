package com.frontegg.sdk.middleware.spring.core.builders;

import com.frontegg.sdk.middleware.spring.filter.FronteggFilter;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.Filter;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class FronteggFilterComparator implements Comparator<Filter>, Serializable {

    private static final int INITIAL_ORDER = 500;
    private static final int ORDER_STEP = 100;
    private final Map<String, Integer> filterToOrder = new HashMap<>();

    FronteggFilterComparator() {
        Step order = new Step(INITIAL_ORDER, ORDER_STEP);
        put(CorsFilter.class, order.next());
        put(FronteggFilter.class, order.next());
    }

    public int compare(Filter lhs, Filter rhs) {
        Integer left = getOrder(lhs.getClass());
        Integer right = getOrder(rhs.getClass());
        return left - right;
    }

    public boolean isRegistered(Class<? extends Filter> filter) {
        return getOrder(filter) != null;
    }

    public void registerAt(Class<? extends Filter> filter, int position) {
        put(filter, position);
    }

    private void put(Class<? extends Filter> filter, int position) {
        String className = filter.getName();
        filterToOrder.put(className, position);
    }

    private Integer getOrder(Class<?> clazz) {
        while (clazz != null) {
            Integer result = filterToOrder.get(clazz.getName());
            if (result != null) {
                return result;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    private static class Step {

        private int value;
        private final int stepSize;

        Step(int initialValue, int stepSize) {
            this.value = initialValue;
            this.stepSize = stepSize;
        }

        int next() {
            int value = this.value;
            this.value += this.stepSize;
            return value;
        }

    }
}

package com.frontegg.sdk.common.util;

import java.util.Collection;
import java.util.List;

public class StringHelper {

    public static boolean isBlank(String val) {
        return isBlank(val, false);
    }

    public static boolean isBlank(String val, boolean trim) {
        return val == null || (trim ? val.trim().length() == 0 : val.length() == 0);
    }

    public static String stringValueOf(Object o) {
        if (o instanceof String) return (String)o;
        if (o instanceof Object[]) {
            Object[] array = (Object[])o;
            if (array.length > 1) {
                StringBuilder builder = new StringBuilder();
                for (Object o1 : array) {
                    builder.append(stringValueOf(o1)).append(",");
                }
                return builder.toString();
            }
            return stringValueOf(array[0]);
        }

        return o.toString();
    }

    public static boolean startWithAny(Collection<String> routs, String url) {
        return routs.stream().filter(s -> s.startsWith(url)).findAny().isPresent();
    }
}

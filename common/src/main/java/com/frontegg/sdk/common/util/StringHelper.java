package com.frontegg.sdk.common.util;

public class StringHelper {

    public static boolean isBlank(String val) {
        return isBlank(val, false);
    }

    public static boolean isBlank(String val, boolean trim) {
        return val == null || (trim ? val.trim().length() == 0 : val.length() == 0);
    }
}

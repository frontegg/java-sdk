package com.frontegg.sdk.common.util;

public class StringHelper {

    public static boolean isBlank(String val) {
        return val == null || val.length() == 0;
    }
}

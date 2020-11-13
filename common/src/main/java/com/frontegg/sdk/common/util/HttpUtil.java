package com.frontegg.sdk.common.util;

public class HttpUtil {

    public static String getRequestUrl(String path, String excludeContextPath) {
        return path.substring(excludeContextPath.length());
    }
}

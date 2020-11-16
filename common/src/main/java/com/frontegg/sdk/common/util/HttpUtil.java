package com.frontegg.sdk.common.util;

import com.frontegg.sdk.common.model.FronteggHttpHeader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

public class HttpUtil {

    public static final String FRONTEGG_HEADER_ACCESS_TOKEN = "x-access-token";
    public static final String FRONTEGG_HEADER_TENANT_ID = "frontegg-tenant-id";
    public static final String FRONTEGG_HEADER_USER_ID = "frontegg-user-id";
    public static final String FRONTEGG_HEADER_VENDOR_HOST = "frontegg-vendor-host";

    public static String getRequestUrl(String path, String excludeContextPath) {
        return path.substring(excludeContextPath.length());
    }

    public static String getHostnameFromRequest(HttpServletRequest request){
        return "";
    }

    public static String getHeader(HttpServletRequest request, String headerName) {
        return getHeader(request, headerName, null);
    }

    public static String getHeader(HttpServletRequest request, String headerName, String defaultValue) {
        String val = request.getHeader(headerName);
        return val == null ? defaultValue : val;
    }

    public static void replaceHeader(List<FronteggHttpHeader> requestHeaders, HttpServletResponse response, String headerName) {
        replaceHeader(requestHeaders, response, headerName, headerName);
    }

    public static void replaceHeader(List<FronteggHttpHeader> requestHeaders, HttpServletResponse response, String headerName, String replaceWithHeader) {
        Optional<FronteggHttpHeader> fronteggHttpHeaderOptional = requestHeaders.stream().filter(f -> f.getName().equals(headerName)).findAny();
        if (fronteggHttpHeaderOptional.isPresent()) response.setHeader(replaceWithHeader, fronteggHttpHeaderOptional.get().getValue());
    }

    public static void deleteHeaders(HttpServletResponse response, String ...headers) {
        for (String header : headers) {
            response.setHeader(header, null);
        }
    }
}

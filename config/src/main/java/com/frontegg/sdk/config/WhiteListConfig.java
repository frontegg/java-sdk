package com.frontegg.sdk.config;

import java.util.Arrays;
import java.util.List;

public class WhiteListConfig {

    private List<String> urls;

    private WhiteListConfig() {}

    public static WhiteListConfig of(String[] whitelistUrls) {
        WhiteListConfig whiteListConfig = new WhiteListConfig();
        whiteListConfig.urls = Arrays.asList(whitelistUrls);
        return whiteListConfig;
    }

    public static WhiteListConfig of(List<String> whitelistUrls) {
        WhiteListConfig whiteListConfig = new WhiteListConfig();
        whiteListConfig.urls = whitelistUrls;
        return whiteListConfig;
    }

    public List<String> getUrls() {
        return urls;
    }
}

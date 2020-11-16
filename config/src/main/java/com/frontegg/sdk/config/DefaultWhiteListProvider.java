package com.frontegg.sdk.config;

import java.util.Arrays;
import java.util.List;

public class DefaultWhiteListProvider implements WhiteListProvider {

    private static final List<String> DEFAULT_WHITELIST_URLS = Arrays.asList("/metadata");

    @Override
    public WhiteListConfig resolveConfigs() {
        return WhiteListConfig.of(DEFAULT_WHITELIST_URLS);
    }
}

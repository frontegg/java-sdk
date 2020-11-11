package com.frontegg.sdk.config;

public class DefaultConfigProvider implements ConfigProvider {
    @Override
    public FronteggConfig resolveConfigs() {
        return FronteggConfig.createDefaults();
    }
}

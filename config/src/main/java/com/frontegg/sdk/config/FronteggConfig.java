package com.frontegg.sdk.config;

public class FronteggConfig {

    private FronteggUrlConfig urlConfig;

    public static FronteggConfig createDefaults() {
        FronteggConfig config = new FronteggConfig();
        config.urlConfig = FronteggUrlConfig.createDefaults();
        return config;
    }

    public void setUrlConfig(FronteggUrlConfig urlConfig) {
        this.urlConfig = urlConfig;
    }

    public FronteggUrlConfig getUrlConfig() {
        return urlConfig;
    }
}

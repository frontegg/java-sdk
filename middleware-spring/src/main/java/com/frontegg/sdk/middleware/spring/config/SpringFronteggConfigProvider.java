package com.frontegg.sdk.middleware.spring.config;

import com.frontegg.sdk.config.ConfigProvider;
import com.frontegg.sdk.config.FronteggConfig;
import org.springframework.stereotype.Component;

@Component
public class SpringFronteggConfigProvider implements ConfigProvider {


    @Override
    public FronteggConfig resolveConfigs() {
        return null;
    }
}

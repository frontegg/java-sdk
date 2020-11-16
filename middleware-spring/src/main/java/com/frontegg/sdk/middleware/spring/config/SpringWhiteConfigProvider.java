package com.frontegg.sdk.middleware.spring.config;

import com.frontegg.sdk.config.WhiteListConfig;
import com.frontegg.sdk.config.WhiteListProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(4)
@Component
public class SpringWhiteConfigProvider implements WhiteListProvider {

    @Value("${frontegg.whitelist:#{null}}")
    private String[] whitelistUrls;

    @Override
    public WhiteListConfig resolveConfigs() {
        if (whitelistUrls == null) return null;

        return WhiteListConfig.of(whitelistUrls);
    }
}

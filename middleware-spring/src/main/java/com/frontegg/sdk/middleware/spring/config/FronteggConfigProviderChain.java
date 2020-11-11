package com.frontegg.sdk.middleware.spring.config;

import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.config.ConfigProvider;
import com.frontegg.sdk.config.FronteggConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class FronteggConfigProviderChain implements ConfigProvider {
    private static final Logger logger = LoggerFactory.getLogger(FronteggConfigProviderChain.class);

    private final List<ConfigProvider> configProviders =
            new LinkedList<ConfigProvider>();


    public FronteggConfigProviderChain(ConfigProvider ... providers) {
        if (providers == null || providers.length == 0) {
            throw new IllegalArgumentException("No config providers specified");
        }

        for (ConfigProvider provider : providers) {
            this.configProviders.add(provider);
        }
    }

    @Override
    public FronteggConfig resolveConfigs() {
        List<String> exceptionMessages = null;
        for (ConfigProvider provider : configProviders) {
            try {
                FronteggConfig config = provider.resolveConfigs();

                if (config != null) {
                    logger.debug("Loading config from " + provider.toString());
                    return config;
                }

            } catch (Exception e) {
                // Ignore any exceptions and move onto the next provider
                String message = provider + ": " + e.getMessage();
                logger.debug("Unable to load config from " + message);
                if (exceptionMessages == null) {
                    exceptionMessages = new LinkedList<String>();
                }
                exceptionMessages.add(message);
            }
        }
        throw new FronteggSDKException("Unable to load frontegg configs from any provider in the chain: " + exceptionMessages);
    }
}

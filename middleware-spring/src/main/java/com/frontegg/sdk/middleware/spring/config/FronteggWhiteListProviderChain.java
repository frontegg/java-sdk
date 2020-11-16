package com.frontegg.sdk.middleware.spring.config;

import com.frontegg.sdk.common.exception.FronteggSDKException;
import com.frontegg.sdk.config.WhiteListConfig;
import com.frontegg.sdk.config.WhiteListProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class FronteggWhiteListProviderChain implements WhiteListProvider {
    private static final Logger logger = LoggerFactory.getLogger(FronteggWhiteListProviderChain.class);

    private final List<WhiteListProvider> configProviders = new LinkedList<>();


    public FronteggWhiteListProviderChain(WhiteListProvider ... providers) {
        if (providers == null || providers.length == 0) {
            throw new IllegalArgumentException("No config providers specified");
        }

        for (WhiteListProvider provider : providers) {
            this.configProviders.add(provider);
        }
    }

    @Override
    public WhiteListConfig resolveConfigs() {
        List<String> exceptionMessages = null;
        for (WhiteListProvider provider : configProviders) {
            try {
                WhiteListConfig config = provider.resolveConfigs();

                if (config != null) {
                    logger.debug("Loading config from " + provider.toString());
                    return config;
                }

            } catch (Exception e) {
                // Ignore any exceptions and move onto the next provider
                String message = provider + ": " + e.getMessage();
                logger.debug("Unable to load config from " + message);
                if (exceptionMessages == null) {
                    exceptionMessages = new LinkedList<>();
                }
                exceptionMessages.add(message);
            }
        }
        throw new FronteggSDKException("Unable to load whitelist configs from any provider in the chain: " + exceptionMessages);
    }
}

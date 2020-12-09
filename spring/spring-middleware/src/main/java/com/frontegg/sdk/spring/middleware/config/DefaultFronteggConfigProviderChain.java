package com.frontegg.sdk.spring.middleware.config;

import com.frontegg.sdk.config.DefaultConfigProvider;
import com.frontegg.sdk.config.EnvironmentVariableConfigProvider;
import com.frontegg.sdk.config.SystemPropertiesConfigProvider;

public class DefaultFronteggConfigProviderChain extends FronteggConfigProviderChain
{
	private static final DefaultFronteggConfigProviderChain INSTANCE = new DefaultFronteggConfigProviderChain();

	public DefaultFronteggConfigProviderChain()
	{
		super(new EnvironmentVariableConfigProvider(),
			  new SystemPropertiesConfigProvider(),
			  new DefaultConfigProvider());
	}

	public static DefaultFronteggConfigProviderChain getInstance() {
		return INSTANCE;
	}
}

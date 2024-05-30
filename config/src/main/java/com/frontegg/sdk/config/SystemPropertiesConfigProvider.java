package com.frontegg.sdk.config;

import java.util.Optional;

public class SystemPropertiesConfigProvider extends BaseConfigProvider
{
	@Override
	protected Optional<String> getBaseUrl(String key)
	{
		return Optional.ofNullable(System.getProperty(key));
	}
}

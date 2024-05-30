package com.frontegg.sdk.config;

import java.util.Optional;

public class EnvironmentVariableConfigProvider extends BaseConfigProvider
{
	@Override
	protected Optional<String> getBaseUrl(String key)
	{
		return Optional.ofNullable(System.getenv(key));
	}
}

package com.frontegg.sdk.config;

public class EnvironmentVariableConfigProvider extends BaseConfigProvider
{
	@Override
	protected String getBaseUrl(String key)
	{
		return System.getenv(key).trim();
	}
}

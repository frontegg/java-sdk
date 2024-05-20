package com.frontegg.sdk.config;

public class SystemPropertiesConfigProvider extends BaseConfigProvider
{
	@Override
	protected String getBaseUrl(String key)
	{
		return System.getProperty(key).trim();
	}
}

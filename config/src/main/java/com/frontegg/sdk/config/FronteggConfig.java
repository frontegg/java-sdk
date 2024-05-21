package com.frontegg.sdk.config;

public class FronteggConfig
{

	private FronteggUrlConfig urlConfig;

	public static FronteggConfig createDefaults()
	{
		var config = new FronteggConfig();
		config.urlConfig = FronteggUrlConfig.createDefaults();
		return config;
	}

	public FronteggUrlConfig getUrlConfig()
	{
		return this.urlConfig;
	}

	public void setUrlConfig(FronteggUrlConfig urlConfig)
	{
		this.urlConfig = urlConfig;
	}
}

package com.frontegg.sdk.middleware;


public class FronteggOptions
{
	private final String clientId;
	private final String apiKey;
	private final boolean disableCors;
	private final String cookieDomainRewrite;
	private final int maxRetries;
	private final String basePath;

	public FronteggOptions(
			String clientId,
			String apiKey,
			boolean disableCors,
			String cookieDomainRewrite,
			int maxRetries,
			String basePath
	)
	{
		this.clientId = clientId;
		this.apiKey = apiKey;
		this.disableCors = disableCors;
		this.cookieDomainRewrite = cookieDomainRewrite;
		this.maxRetries = maxRetries;
		this.basePath = basePath;
	}

	public String getClientId()
	{
		return this.clientId;
	}

	public String getApiKey()
	{
		return this.apiKey;
	}

	public boolean isDisableCors()
	{
		return this.disableCors;
	}

	public String getCookieDomainRewrite()
	{
		return this.cookieDomainRewrite;
	}

	public int getMaxRetries()
	{
		return this.maxRetries;
	}

	public String getBasePath()
	{
		return this.basePath;
	}
}

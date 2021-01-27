package com.frontegg.sdk.sso;

public class SamlVendorConfig
{
	private String spEntityId;
	private String acsUrl;

	public SamlVendorConfig()
	{
	}

	public SamlVendorConfig(String spEntityId, String acsUrl)
	{
		this.spEntityId = spEntityId;
		this.acsUrl = acsUrl;
	}

	public String getSpEntityId()
	{
		return this.spEntityId;
	}

	public String getAcsUrl()
	{
		return this.acsUrl;
	}
}

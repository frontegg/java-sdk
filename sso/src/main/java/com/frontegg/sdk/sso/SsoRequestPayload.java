package com.frontegg.sdk.sso;

public class SsoRequestPayload
{
	private String payload;

	public SsoRequestPayload()
	{
	}

	public SsoRequestPayload(String payload)
	{
		this.payload = payload;
	}

	public String getPayload()
	{
		return this.payload;
	}
}
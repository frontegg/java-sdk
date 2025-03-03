package com.frontegg.sdk.sso;

public class SsoRequestWithPayload
{
	private String payload;


	public SsoRequestWithPayload(String payload)
	{
		this.payload = payload;
	}

	public String getPayload()
	{
		return this.payload;
	}
}
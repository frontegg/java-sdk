package com.frontegg.sdk.sso;

public class SsoRequest
{
	private String payload;

	public SsoRequest()
	{
	}

	public SsoRequest(String email)
	{
		this.payload = email;
	}

	public String getPayload()
	{
		return this.payload;
	}
}

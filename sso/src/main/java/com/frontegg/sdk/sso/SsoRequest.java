package com.frontegg.sdk.sso;

public class SsoRequest
{
	private String email;

	public SsoRequest()
	{
	}

	public SsoRequest(String payload)
	{
		this.email = payload;
	}

	public String getEmail()
	{
		return this.email;
	}
}

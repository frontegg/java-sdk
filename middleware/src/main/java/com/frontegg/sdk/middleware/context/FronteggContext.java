package com.frontegg.sdk.middleware.context;

public class FronteggContext
{
	private final String tenantId;
	private final String userId;

	public FronteggContext(String tenantId, String userId)
	{
		this.tenantId = tenantId;
		this.userId = userId;
	}

	public String getTenantId()
	{
		return this.tenantId;
	}

	public String getUserId()
	{
		return this.userId;
	}
}

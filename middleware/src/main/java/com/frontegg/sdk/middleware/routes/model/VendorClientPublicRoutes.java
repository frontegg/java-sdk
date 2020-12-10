package com.frontegg.sdk.middleware.routes.model;

import java.util.List;

public class VendorClientPublicRoutes
{
	private String method;
	private String url;
	private String description;
	private List<KeyValPair> withQueryParams;

	public String getMethod()
	{
		return this.method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public String getUrl()
	{
		return this.url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public List<KeyValPair> getWithQueryParams()
	{
		return this.withQueryParams;
	}

	public void setWithQueryParams(List<KeyValPair> withQueryParams)
	{
		this.withQueryParams = withQueryParams;
	}
}

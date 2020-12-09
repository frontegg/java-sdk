package com.frontegg.sdk.events.types;

public class BellAction
{

	/**
	 * Display name of the action.
	 */
	private String name;

	/**
	 * Url that the request will be sent to when clicking on the action.
	 */
	private String url;

	/**
	 * Request method.
	 */
	private String method;

	/**
	 * Determent how to render the action.
	 */
	private Visualization visualization;

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getUrl()
	{
		return this.url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getMethod()
	{
		return this.method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public Visualization getVisualization()
	{
		return this.visualization;
	}

	public void setVisualization(Visualization visualization)
	{
		this.visualization = visualization;
	}
}

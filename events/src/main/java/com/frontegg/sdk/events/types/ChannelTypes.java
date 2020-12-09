package com.frontegg.sdk.events.types;

public enum ChannelTypes
{
	SLACK("slack"),
	WEBHOOK("webhook"),
	WEBPUSH("webpush"),
	AUDIT("audit"),
	BELL("bell");

	private final String name;

	ChannelTypes(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}
}

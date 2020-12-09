package com.frontegg.sdk.events.types;

public enum Severity
{
	INFO("Info"),
	MEDIUM("Medium"),
	HIGH("High"),
	CRITICAL("Critical");

	private final String name;

	Severity(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}
}

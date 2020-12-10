package com.frontegg.sdk.events.model;

import java.util.Map;

public class EventStatuses
{
	private String eventKey;
	private String eventId;
	private Map<String, Object> channels; // sorry users

	public EventStatuses()
	{
	}

	public String getEventKey()
	{
		return this.eventKey;
	}

	public void setEventKey(String eventKey)
	{
		this.eventKey = eventKey;
	}

	public String getEventId()
	{
		return this.eventId;
	}

	public void setEventId(String eventId)
	{
		this.eventId = eventId;
	}

	public Map<String, Object> getChannels()
	{
		return this.channels;
	}

	public void setChannels(Map<String, Object> channels)
	{
		this.channels = channels;
	}
}

package com.frontegg.sdk.events.types;

public class TriggerOptions<T extends EventProperties>
{
	/**
	 * Event key to trigger channel configuration by.
	 */
	private String eventKey;

	/**
	 * Default properties for all the channels - can be override in the channel configuration.
	 */
	private T properties;

	/**
	 * trigger the event for a specific tenantId.
	 */
	private String tenantId;

	/**
	 * configuration of the channels the event will be sent to.
	 */
	private ChannelsConfiguration channels;

	public String getEventKey()
	{
		return this.eventKey;
	}

	public void setEventKey(String eventKey)
	{
		this.eventKey = eventKey;
	}

	public EventProperties getProperties()
	{
		return this.properties;
	}

	public void setProperties(T properties)
	{
		this.properties = properties;
	}

	public String getTenantId()
	{
		return this.tenantId;
	}

	public void setTenantId(String tenantId)
	{
		this.tenantId = tenantId;
	}

	public ChannelsConfiguration getChannels()
	{
		return this.channels;
	}

	public void setChannels(ChannelsConfiguration channels)
	{
		this.channels = channels;
	}
}

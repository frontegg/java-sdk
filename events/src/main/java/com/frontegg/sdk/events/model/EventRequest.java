package com.frontegg.sdk.events.model;

import com.frontegg.sdk.events.types.ChannelTypes;
import com.frontegg.sdk.events.types.ChannelsConfiguration;
import com.frontegg.sdk.events.types.EventProperties;
import com.frontegg.sdk.events.types.TriggerOptions;

public class EventRequest {

    private String eventKey;

    private EventProperties properties;

    private ChannelsConfiguration channels;

    public static <T extends EventProperties>  EventRequest fromTriggerOptions(TriggerOptions<T> options) {
        EventRequest request = new EventRequest();
        request.setChannels(options.getChannels());
        request.setEventKey(options.getEventKey());
        request.setProperties(options.getProperties());
        return request;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public EventProperties getProperties() {
        return properties;
    }

    public void setProperties(EventProperties properties) {
        this.properties = properties;
    }

    public ChannelsConfiguration getChannels() {
        return channels;
    }

    public void setChannels(ChannelsConfiguration channels) {
        this.channels = channels;
    }
}

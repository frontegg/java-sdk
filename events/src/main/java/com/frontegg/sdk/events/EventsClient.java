package com.frontegg.sdk.events;

import com.frontegg.sdk.events.model.EventResponse;
import com.frontegg.sdk.events.model.EventStatuses;
import com.frontegg.sdk.events.types.EventChannelStatus;
import com.frontegg.sdk.events.types.TriggerOptions;

import java.util.concurrent.CompletableFuture;

public interface EventsClient {

    EventResponse trigger(TriggerOptions options);

    EventStatuses getEventStatus(String eventId);

    CompletableFuture<EventChannelStatus> waitForEventStatus(String eventId);
}

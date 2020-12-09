package com.frontegg.sdk.events;

import com.frontegg.sdk.events.model.EventResponse;
import com.frontegg.sdk.events.model.EventStatuses;
import com.frontegg.sdk.events.types.EventProperties;
import com.frontegg.sdk.events.types.TriggerOptions;

public interface EventsClient {

    EventResponse trigger(TriggerOptions<? super EventProperties> options);

    EventStatuses getEventStatus(String eventId);
}

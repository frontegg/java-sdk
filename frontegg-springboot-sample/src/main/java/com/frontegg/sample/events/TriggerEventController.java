package com.frontegg.sample.events;

import com.frontegg.sdk.events.EventsClient;
import com.frontegg.sdk.events.model.EventResponse;
import com.frontegg.sdk.events.model.EventStatuses;
import com.frontegg.sdk.events.types.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/events")
public class TriggerEventController {

    @Autowired
    private EventsClient eventsClient;

    @PostMapping("/trigger")
    public ResponseEntity<EventResponse> triggerEvents(@RequestParam String eventKey,
                                                       @RequestParam String tenantId,
                                                       @RequestParam String title,
                                                       @RequestParam String description) {

        EventProperties eventProperties = new DefaultEventProperties(title, description);
        TriggerOptions<EventProperties> options = new TriggerOptions<>();
        ChannelsConfiguration channelsConfiguration = new ChannelsConfigurationBuilder()
                .defaultWebhook().build();
        options.setChannels(channelsConfiguration);
        options.setEventKey(eventKey);
        options.setTenantId(tenantId);
        options.setProperties(eventProperties);
        EventResponse response = eventsClient.trigger(options);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventStatuses> getEventStatus(@PathVariable String eventId) {
        EventStatuses statuses = eventsClient.getEventStatus(eventId);
        return new ResponseEntity<>(statuses, HttpStatus.OK);
    }
}

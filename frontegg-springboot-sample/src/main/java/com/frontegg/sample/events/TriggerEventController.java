package com.frontegg.sample.events;

import com.frontegg.sdk.events.EventsClient;
import com.frontegg.sdk.events.model.EventResponse;
import com.frontegg.sdk.events.model.EventStatuses;
import com.frontegg.sdk.events.types.*;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.context.FronteggContextResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/events")
public class TriggerEventController {

    @Autowired
    private EventsClient eventsClient;

    @Autowired
    private FronteggContextResolver fronteggContextResolver;

    @RequestMapping(value = "/trigger",
                    method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<EventResponse> triggerEvents(@RequestParam String eventKey,
                                                       @RequestParam String title,
                                                       @RequestParam String description) {

        FronteggContext fronteggContext = fronteggContextResolver.resolveContext();

        EventProperties eventProperties = new DefaultEventProperties(title, description);
        TriggerOptions<EventProperties> options = new TriggerOptions<>();
        ChannelsConfiguration channelsConfiguration = new ChannelsConfigurationBuilder()
                .defaultWebhook().build();
        options.setChannels(channelsConfiguration);
        options.setEventKey(eventKey);
        options.setTenantId(fronteggContext.getTenantId());
        options.setProperties(eventProperties);
        EventResponse response = eventsClient.trigger(options);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{eventId}",
                    method = RequestMethod.GET,
                    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<EventStatuses> getEventStatus(@PathVariable String eventId) {
        EventStatuses statuses = eventsClient.getEventStatus(eventId);
        return new ResponseEntity<>(statuses, HttpStatus.OK);
    }
}

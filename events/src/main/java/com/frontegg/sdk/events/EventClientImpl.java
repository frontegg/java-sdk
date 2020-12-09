package com.frontegg.sdk.events;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.common.exception.InvalidParameterException;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.events.model.EventRequest;
import com.frontegg.sdk.events.model.EventResponse;
import com.frontegg.sdk.events.model.EventStatuses;
import com.frontegg.sdk.events.types.TriggerOptions;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.frontegg.sdk.common.util.HttpHelper.FRONTEGG_HEADER_ACCESS_TOKEN;
import static com.frontegg.sdk.common.util.HttpHelper.FRONTEGG_HEADER_TENANT_ID;

public class EventClientImpl implements EventsClient {

    private static final Logger logger = LoggerFactory.getLogger(EventClientImpl.class);

    private FronteggAuthenticator authenticator;
    private ApiClient apiClient;
    private FronteggConfig config;

    private static final String TRIGGER_PATH = "/resources/triggers/v2";
    private static final String TRIGGER_STATUSES_PATH = "/resources/triggers/v2/statuses/";

    public EventClientImpl(FronteggAuthenticator authenticator, ApiClient apiClient, FronteggConfig config) {
        this.authenticator = authenticator;
        this.apiClient = apiClient;
        this.config = config;
    }

    public EventResponse trigger(TriggerOptions options) {
        validateTriggerOptions(options);

        try {
            logger.info("going to trigger event");
            authenticator.validateAuthentication();
            Map<String, String> headers = resolveHeaders(options.getTenantId());
            EventRequest eventRequest = EventRequest.fromTriggerOptions(options);
            FronteggHttpResponse<EventResponse> response =
                    apiClient.post(config.getUrlConfig().getEventService() + TRIGGER_PATH,
                    EventResponse.class,
                    headers,
                    eventRequest
            );
            logger.info("triggered event successfully");
            return response.getBody();
        } catch (Exception e) {
            logger.error("failed to trigger event ", e);
            throw e;
        }
    }

    @Override
    public EventStatuses getEventStatus(String eventId) {
        authenticator.validateAuthentication();
        Map<String, String> headers = resolveHeaders();
        Optional<EventStatuses> eventStatuses = apiClient.get(
                config.getUrlConfig().getEventService() + TRIGGER_STATUSES_PATH  + eventId,
                headers,
                EventStatuses.class);
        return eventStatuses.get();
    }

    private Map<String, String> resolveHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(FRONTEGG_HEADER_ACCESS_TOKEN, authenticator.getAccessToken());
        return headers;
    }

    private Map<String, String> resolveHeaders(String tenantID) {
        Map<String, String> headers = new HashMap<>();
        headers.put(FRONTEGG_HEADER_ACCESS_TOKEN, authenticator.getAccessToken());
        headers.put(FRONTEGG_HEADER_TENANT_ID, tenantID);
        return headers;
    }

    private void validateTriggerOptions(TriggerOptions options) {
        if (StringHelper.isBlank(options.getEventKey())) {
            logger.warn("eventKey is required");
            throw new InvalidParameterException("eventKey is required");
        }

        if (options.getChannels() == null || !options.getChannels().hasValidConfiguration() ) {
            logger.warn("At least one channel should be configured");
            throw new Error("At least one channel should be configured");
        }

        if (options.getProperties() == null) {
            logger.warn("eventKey is required");
            throw new Error("eventKey is required");
        }

        if (StringHelper.isBlank(options.getProperties().getTitle())) {
            logger.warn("properties.title is required");
            throw new Error("properties.title is required");
        }

        if (StringHelper.isBlank(options.getProperties().getDescription())) {
            logger.warn("properties.description is required");
            throw new Error("properties.description is required");
        }
    }
}

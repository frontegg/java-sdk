package com.frontegg.sdk.events.channels;

import com.frontegg.sdk.events.types.BellAction;
import com.frontegg.sdk.events.types.Severity;

import java.time.LocalDateTime;

public interface BellProperties {

    String getUserId();

    String getTitle();

    String getBody();

    Severity getSeverity();

    LocalDateTime getExpiryDate();

    String getUrl();

    BellAction[] getActions();
}

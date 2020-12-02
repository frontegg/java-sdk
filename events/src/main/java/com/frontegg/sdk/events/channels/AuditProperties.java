package com.frontegg.sdk.events.channels;

import com.frontegg.sdk.events.types.Severity;

import java.time.LocalDateTime;

public interface AuditProperties {

    /**
     * Set audit creation time, default value is the time audit accepted.
     */
    LocalDateTime getCreatedAt();

    /**
     * Set audit severity, default value is "Info".
     */
    Severity getSeverity();
}

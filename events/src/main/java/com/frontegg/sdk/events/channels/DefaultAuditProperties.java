package com.frontegg.sdk.events.channels;

import com.frontegg.sdk.events.types.Severity;

import java.time.LocalDateTime;
import java.util.Map;

public class DefaultAuditProperties implements AuditProperties {

    /**
     * Set audit creation time, default value is the time audit accepted.
     */
    private LocalDateTime createdAt;

    /**
     * Set audit severity, default value is "Info".
     */
    private Severity severity;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }
}

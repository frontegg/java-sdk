package com.frontegg.sdk.events.channels;

import com.frontegg.sdk.events.types.BellAction;
import com.frontegg.sdk.events.types.Severity;

import java.time.LocalDateTime;

public class DefaultBellProperties implements BellProperties {

    /**
     * Send the bell notification to specific user, by his ID.
     */
    private String userId;

    /**
     * Notification title.
     */
    private String title;

    /**
     * Notification body.
     */
    private String body;

    /**
     * Notification severity, default will be Info.
     */
    private Severity severity;

    /**
     * Notification expiration Date, by default the notification won't have expiration date.
     */
    private LocalDateTime expiryDate;

    /**
     * The url that will be opened on a new window on click.
     */
    private String url;

    /**
     * Actions array that will be shown in the notification.
     */
    private BellAction[] actions;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BellAction[] getActions() {
        return actions;
    }

    public void setActions(BellAction[] actions) {
        this.actions = actions;
    }
}

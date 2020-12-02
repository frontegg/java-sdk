package com.frontegg.sdk.events.types;

import com.frontegg.sdk.events.channels.*;

public class ChannelsConfiguration {

    /**
     * True to use default properties or set properties for this channel.
     */
    private SlackProperties slack;

    /**
     * True to send default properties in the body or add additional properties.
     */
    private WebhookBody webhook;

    /**
     * True to use default properties or set properties for this channel.
     */
    private WebpushProperties webpush;

    /**
     * True to use default properties or set properties for this channel.
     */
    private AuditProperties audit;

    /**
     * True to use default properties or set properties for this channel.
     */
    private BellProperties bell;

    public ChannelsConfiguration() {}

    public SlackProperties getSlack() {
        return slack;
    }

    public void setSlack(SlackProperties slack) {
        this.slack = slack;
    }

    public WebhookBody getWebhook() {
        return webhook;
    }

    public void setWebhook(WebhookBody webhook) {
        this.webhook = webhook;
    }

    public WebpushProperties getWebpush() {
        return webpush;
    }

    public void setWebpush(WebpushProperties webpush) {
        this.webpush = webpush;
    }

    public AuditProperties getAudit() {
        return audit;
    }

    public void setAudit(AuditProperties audit) {
        this.audit = audit;
    }

    public BellProperties getBell() {
        return bell;
    }

    public void setBell(BellProperties bell) {
        this.bell = bell;
    }

    public boolean hasValidConfiguration() {
        return slack != null || audit != null || webpush != null || webhook != null || bell != null;
    }

}

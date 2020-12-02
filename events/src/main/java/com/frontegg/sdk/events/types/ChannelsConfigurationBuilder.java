package com.frontegg.sdk.events.types;

import com.frontegg.sdk.events.channels.*;

public class ChannelsConfigurationBuilder {
    private SlackProperties slack;
    private WebhookBody webhook;
    private WebpushProperties webpush;
    private AuditProperties audit;
    private BellProperties bell;

    public ChannelsConfigurationBuilder defaultSlack() {
        slack = new DefaultSlackProperties();
        return this;
    }

    public ChannelsConfigurationBuilder defaultBell() {
        bell = new DefaultBellProperties();
        return this;
    }

    public ChannelsConfigurationBuilder defaultAudit() {
        audit = new DefaultAuditProperties();
        return this;
    }

    public ChannelsConfigurationBuilder defaultWebpush() {
        webpush = new DefaultWebpushProperties();
        return this;
    }

    public ChannelsConfigurationBuilder defaultWebhook() {
        webhook = new DefaultWebhookBody();
        return this;
    }

    public <T extends SlackProperties> ChannelsConfigurationBuilder slack(T slack) {
        this.slack = slack;
        return this;
    }

    public <T extends WebhookBody> ChannelsConfigurationBuilder webhook(T webHook) {
        this.webhook = webHook;
        return this;
    }

    public <T extends WebpushProperties> ChannelsConfigurationBuilder webpush(T webpush) {
        this.webpush = webpush;
        return this;
    }

    public <T extends AuditProperties> ChannelsConfigurationBuilder audit(T audit) {
        this.audit = audit;
        return this;
    }

    public <T extends BellProperties> ChannelsConfigurationBuilder bell(T bell) {
        this.bell = bell;
        return this;
    }

    public ChannelsConfiguration build() {
        ChannelsConfiguration channelsConfiguration = new ChannelsConfiguration();
        channelsConfiguration.setAudit(audit);
        channelsConfiguration.setWebhook(webhook);
        channelsConfiguration.setWebpush(webpush);
        channelsConfiguration.setSlack(slack);
        channelsConfiguration.setBell(bell);
        return channelsConfiguration;
    }
}
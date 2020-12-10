package com.frontegg.sdk.events.types;

import com.frontegg.sdk.events.channels.*;

public class ChannelsConfiguration
{
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

	public SlackProperties getSlack()
	{
		return this.slack;
	}

	public void setSlack(SlackProperties slack)
	{
		this.slack = slack;
	}

	public WebhookBody getWebhook()
	{
		return this.webhook;
	}

	public void setWebhook(WebhookBody webhook)
	{
		this.webhook = webhook;
	}

	public WebpushProperties getWebpush()
	{
		return this.webpush;
	}

	public void setWebpush(WebpushProperties webpush)
	{
		this.webpush = webpush;
	}

	public AuditProperties getAudit()
	{
		return this.audit;
	}

	public void setAudit(AuditProperties audit)
	{
		this.audit = audit;
	}

	public BellProperties getBell()
	{
		return this.bell;
	}

	public void setBell(BellProperties bell)
	{
		this.bell = bell;
	}

	public boolean hasValidConfiguration()
	{
		return this.slack != null || this.audit != null || this.webpush != null || this.webhook != null || this.bell != null;
	}
}

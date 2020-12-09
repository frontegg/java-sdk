package com.frontegg.sdk.events.model;

import com.frontegg.sdk.events.types.EventChannelStatus;

public class EventStatuses
{
	private String eventKey;
	private String eventId;
	private Channels channels;

	public EventStatuses()
	{
	}

	public String getEventKey()
	{
		return eveÒntKey;
	}

	public void setEventKey(String eventKey)
	{
		this.eventKey = eventKey;
	}

	public String getEventId()
	{
		return eventId;
	}

	public void setEventId(String eventId)
	{
		this.eventId = eventId;
	}

	public Channels getChannels()
	{
		return channels;
	}

	public void setChannels(Channels channels)
	{
		this.channels = channels;
	}

	public boolean isThereStatusEqualTo(EventChannelStatus status)
	{
		return getChannels().sms.status == status || getChannels().slack.status == status || getChannels().email.status == status;
	}

	public boolean isAllStatusesEqualTo(EventChannelStatus status)
	{
		return getChannels().sms.status == status && getChannels().slack.status == status && getChannels().email.status == status;
	}

	class Channels
	{
		private Slack slack;
		private Email email;
		private Sms sms;

		public Slack getSlack()
		{
			return slack;
		}

		public void setSlack(Slack slack)
		{
			this.slack = slack;
		}

		public Email getEmail()
		{
			return email;
		}

		public void setEmail(Email email)
		{
			this.email = email;
		}

		public Sms getSms()
		{
			return sms;
		}

		public void setSms(Sms sms)
		{
			this.sms = sms;
		}
	}


	class Channel
	{
		protected EventChannelStatus status;

		public EventChannelStatus getStatus()
		{
			return status;
		}

		public void setStatus(EventChannelStatus status)
		{
			this.status = status;
		}
	}

	class SimpleErrorMetadata
	{
		private String error;

		public String getError()
		{
			return error;
		}

		public void setError(String error)
		{
			this.error = error;
		}
	}

	class ChannelErrorMetadata
	{
		private ErrorMetadataByChannel errorsByChannel;

		public ErrorMetadataByChannel getErrorsByChannel()
		{
			return errorsByChannel;
		}

		public void setErrorsByChannel(ErrorMetadataByChannel errorsByChannel)
		{
			this.errorsByChannel = errorsByChannel;
		}
	}

	class ErrorMetadataByChannel
	{
		private String channelId;
		private String error;

		public String getChannelId()
		{
			return channelId;
		}

		public void setChannelId(String channelId)
		{
			this.channelId = channelId;
		}

		public String getError()
		{
			return error;
		}

		public void setError(String error)
		{
			this.error = error;
		}
	}

	class Slack extends Channel
	{
		private ChannelErrorMetadata[] errorMetadata;

		public ChannelErrorMetadata[] getErrorMetadata()
		{
			return errorMetadata;
		}

		public void setErrorMetadata(ChannelErrorMetadata[] errorMetadata)
		{
			this.errorMetadata = errorMetadata;
		}
	}

	class Email extends Channel
	{
		private SimpleErrorMetadata errorMetadata;

		public SimpleErrorMetadata getErrorMetadata()
		{
			return errorMetadata;
		}

		public void setErrorMetadata(SimpleErrorMetadata errorMetadata)
		{
			this.errorMetadata = errorMetadata;
		}
	}

	class Sms extends Channel
	{
		private SimpleErrorMetadata errorMetadata;

		public SimpleErrorMetadata getErrorMetadata()
		{
			return errorMetadata;
		}

		public void setErrorMetadata(SimpleErrorMetadata errorMetadata)
		{
			this.errorMetadata = errorMetadata;
		}
	}

}

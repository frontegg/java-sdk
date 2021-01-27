package com.frontegg.sdk.sso;

import java.time.Instant;

public class SamlTenantConfig
{
	private Boolean enabled;
	private String domain;
	private Boolean validated;
	private String generatedVerification;
	private String ssoEndpoint;
	private String publicCertificate;
	private Boolean signRequest;
	private Instant createdAt;
	private Instant updatedAt;
	private String acsUrl;
	private String spEntityId;

	public SamlTenantConfig()
	{
	}

	public SamlTenantConfig(
			Boolean enabled,
			String domain,
			Boolean validated,
			String generatedVerification,
			String ssoEndpoint,
			String publicCertificate,
			Boolean signRequest,
			Instant createdAt,
			Instant updatedAt,
			String acsUrl,
			String spEntityId
	)
	{
		this.enabled = enabled;
		this.domain = domain;
		this.validated = validated;
		this.generatedVerification = generatedVerification;
		this.ssoEndpoint = ssoEndpoint;
		this.publicCertificate = publicCertificate;
		this.signRequest = signRequest;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.acsUrl = acsUrl;
		this.spEntityId = spEntityId;
	}

	public Boolean getEnabled()
	{
		return this.enabled;
	}

	public String getDomain()
	{
		return this.domain;
	}

	public Boolean getValidated()
	{
		return this.validated;
	}

	public String getGeneratedVerification()
	{
		return this.generatedVerification;
	}

	public String getSsoEndpoint()
	{
		return this.ssoEndpoint;
	}

	public String getPublicCertificate()
	{
		return this.publicCertificate;
	}

	public Boolean getSignRequest()
	{
		return this.signRequest;
	}

	public Instant getCreatedAt()
	{
		return this.createdAt;
	}

	public Instant getUpdatedAt()
	{
		return this.updatedAt;
	}

	public String getAcsUrl()
	{
		return this.acsUrl;
	}

	public String getSpEntityId()
	{
		return this.spEntityId;
	}
}

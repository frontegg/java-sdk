package com.frontegg.sdk.audit;

import com.frontegg.sdk.audit.model.AuditsFilter;

import java.util.List;

public interface IAuditsClient
{
    void sendAudit(Object audit);

    List<Object> getAudits(AuditsFilter auditsFilter);

    Object getAuditsStats(String tenantId);

    Object getAuditsMetadata();

    Object setAuditsMetadata(Object metadata);
}

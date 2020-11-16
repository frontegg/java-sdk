package com.frontegg.sdk.audit;

import com.frontegg.sdk.audit.model.AuditFilter;

import java.util.List;

public interface IAuditClient {

    void sendAudit(Object audit);

    List<Object> getAudits(AuditFilter auditFilter);

    Object getAuditsStats(String tenantId);

    Object getAuditsMetadata();

    Object setAuditsMetadata(Object metadata);

    Object exportPdf(AuditFilter auditFilter, String[] properties);

    Object exportCsv(AuditFilter auditFilter, String[] properties);
}

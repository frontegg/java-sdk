package com.frontegg.sdk.audit;

import com.frontegg.sdk.audit.model.AuditFilter;
import com.frontegg.sdk.audit.model.Auditable;
import com.frontegg.sdk.audit.model.MetadataObject;

import java.util.List;

public interface AuditClient {

    void sendAudit(Auditable audit);

    List<Object> getAudits(AuditFilter auditFilter);

    Object getAuditsStats(String tenantId);

    Object getAuditsMetadata();

    Object setAuditsMetadata(MetadataObject metadata);

    Object exportPdf(AuditFilter auditFilter, String[] properties);

    Object exportCsv(AuditFilter auditFilter, String[] properties);
}

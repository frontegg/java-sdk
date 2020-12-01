package com.frontegg.sdk.audit;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.audit.model.AuditFilter;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AuditClient implements IAuditClient {
    private static final Logger logger = LoggerFactory.getLogger(AuditClient.class);
    private FronteggAuthenticator authenticator;
    private ApiClient apiClient;
    private FronteggConfig config;

    public AuditClient(FronteggAuthenticator authenticator, ApiClient apiClient, FronteggConfig config) {
        this.authenticator = authenticator;
        this.apiClient = apiClient;
        this.config = config;
    }

    @Override
    public void sendAudit(Object audits) {
        try {
            logger.info("going to send audit");
            authenticator.validateAuthentication();
            FronteggHttpResponse<Object> optional = apiClient.post(config.getUrlConfig().getAuditsService(), Object.class, audits);
            logger.info("sent audit successfully - " + optional.getBody());

        } catch (Exception e) {
            logger.error("failed to send audit to audits service - ", e);
            throw e;
        }
    }

    @Override
    public List<Object> getAudits(AuditFilter auditFilter) {
        return null;
    }

    @Override
    public Object getAuditsStats(String tenantId) {
        return null;
    }

    @Override
    public Object getAuditsMetadata() {
        return null;
    }

    @Override
    public Object setAuditsMetadata(Object metadata) {
        return null;
    }

    @Override
    public Object exportPdf(AuditFilter auditFilter, String[] properties) {
        return null;
    }

    @Override
    public Object exportCsv(AuditFilter auditFilter, String[] properties) {
        return null;
    }
}

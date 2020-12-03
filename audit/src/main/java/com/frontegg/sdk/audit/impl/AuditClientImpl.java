package com.frontegg.sdk.audit.impl;

import com.frontegg.sdk.api.client.ApiClient;
import com.frontegg.sdk.audit.AuditClient;
import com.frontegg.sdk.audit.model.AuditFilter;
import com.frontegg.sdk.audit.model.Auditable;
import com.frontegg.sdk.audit.model.MetadataObject;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.config.FronteggConfig;
import com.frontegg.sdk.middleware.authenticator.FronteggAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.frontegg.sdk.common.util.HttpHelper.FRONTEGG_HEADER_ACCESS_TOKEN;
import static com.frontegg.sdk.common.util.HttpHelper.FRONTEGG_HEADER_TENANT_ID;

public class AuditClientImpl implements AuditClient {

    private static final Logger logger = LoggerFactory.getLogger(AuditClientImpl.class);
    private FronteggAuthenticator authenticator;
    private ApiClient apiClient;
    private FronteggConfig config;

    private static final String EXPORT_CSV_PATH = "/export/csv";
    private static final String EXPORT_PDF_PATH = "/export/pdf";
    private static final String AUDIT_STATS_PATH = "/stats";

    public AuditClientImpl(FronteggAuthenticator authenticator, ApiClient apiClient, FronteggConfig config) {
        this.authenticator = authenticator;
        this.apiClient = apiClient;
        this.config = config;
    }

    @Override
    public void sendAudit(Auditable audit) {
        try {
            logger.info("going to send audit");
            authenticator.validateAuthentication();
            Map<String, String> headers = resolveHeaders(audit.getTenantId());
            FronteggHttpResponse<Object> optional = apiClient.post(
                    config.getUrlConfig().getAuditsService(),
                    Object.class,
                    headers,
                    audit
            );
            logger.info("sent audit successfully - " + optional.getBody());

        } catch (Exception e) {
            logger.error("failed to send audit to audits service - ", e);
            throw e;
        }
    }

    @Override
    public List<Object> getAudits(AuditFilter auditFilter) {
        logger.info("going to get audits");
        authenticator.validateAuthentication();
        Map<String, String> params = resolveFilters(auditFilter);
        Map<String, String> headers = resolveHeaders(auditFilter.getTenantId());
        Optional<List> response = apiClient.get(
                config.getUrlConfig().getAuditsService(),
                headers,
                params,
                List.class
        );
        return response.get();
    }

    @Override
    public Object getAuditsStats(String tenantId) {
        logger.info("going to get audits stats");

        authenticator.validateAuthentication();
        Map<String, String> headers = resolveHeaders(tenantId);
        Optional<Object> response = apiClient.get(
                config.getUrlConfig().getAuditsService() + AUDIT_STATS_PATH,
                headers,
                Object.class
        );
        return response.get();
    }

    @Override
    public Object getAuditsMetadata() {
        logger.info("going to get audits metadata");
        Map<String, String> params = new HashMap<>();
        params.put("entityName", "audits");
        authenticator.validateAuthentication();
        Map<String, String> headers = resolveHeaders();
        Optional<Object> response = apiClient.get(
                config.getUrlConfig().getMetadataService(),
                headers,
                params,
                Object.class
        );

        logger.info("got audits metadata");
        return response.get();
    }

    @Override
    public Object setAuditsMetadata(MetadataObject metadata) {
        // Make sure to override the entity name
        metadata.setEntityName("audits");
        logger.info("going to update audits metadata");

        authenticator.validateAuthentication();
        Map<String, String> headers = resolveHeaders();
        FronteggHttpResponse<Object> response = apiClient.post(
                config.getUrlConfig().getMetadataService(),
                Object.class,
                headers,
                metadata
        );

        logger.info("done updating audits metadata");
        return response.getBody();
    }

    @Override
    public Object exportPdf(AuditFilter auditFilter, String[] properties) {
        logger.info("going to export audits to pdf");
        authenticator.validateAuthentication();
        Map<String, String> params = resolveFilters(auditFilter);
        Map<String, String> headers = resolveHeaders(auditFilter.getTenantId());
        //headers.put("", "application/octet-stream")
        FronteggHttpResponse<Object> response = apiClient.post(
                config.getUrlConfig().getAuditsService() + EXPORT_PDF_PATH,
                Object.class,
                headers,
                params,
                null
        );

        return response.getBody();
    }

    @Override
    public Object exportCsv(AuditFilter auditFilter, String[] properties) {
        logger.info("going to export audits to csv");
        authenticator.validateAuthentication();
        Map<String, String> params = resolveFilters(auditFilter);
        Map<String, String> headers = resolveHeaders(auditFilter.getTenantId());
        FronteggHttpResponse<Object> response = apiClient.post(
                config.getUrlConfig().getAuditsService() + EXPORT_CSV_PATH,
                Object.class,
                headers,
                params,
                null
        );

        return response.getBody();
    }

    private Map<String, String> resolveHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(FRONTEGG_HEADER_ACCESS_TOKEN, authenticator.getAccessToken());
        return headers;
    }

    private Map<String, String> resolveHeaders(String tenantID) {
        Map<String, String> headers = new HashMap<>();
        headers.put(FRONTEGG_HEADER_ACCESS_TOKEN, authenticator.getAccessToken());
        headers.put(FRONTEGG_HEADER_TENANT_ID, tenantID);
        return headers;
    }

    private Map<String, String> resolveFilters(AuditFilter auditFilter) {
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("filter", auditFilter.getFilter());
        queryParam.put("offset", String.valueOf(auditFilter.getOffset()));
        queryParam.put("count", auditFilter.getSortBy());
        queryParam.put("sortDirection", auditFilter.getSortDirection());
        return queryParam;
    }
}
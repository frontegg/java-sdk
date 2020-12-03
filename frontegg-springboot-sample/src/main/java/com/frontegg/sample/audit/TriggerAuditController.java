package com.frontegg.sample.audit;

import com.frontegg.sdk.audit.AuditClient;
import com.frontegg.sdk.audit.model.AuditFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit")
public class TriggerAuditController {

    @Autowired
    private AuditClient auditClient;

    @PostMapping("/")
    public ResponseEntity<?> sendAudit(@RequestBody AuditModel auditModel) {

        auditClient.sendAudit(auditModel);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/metadata")
    public ResponseEntity<?> setMetadata(@RequestBody AuditMetadata metadata) {

        auditClient.setAuditsMetadata(metadata);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/metadata")
    public ResponseEntity<?> getMetadata() {
        return new ResponseEntity<>(auditClient.getAuditsMetadata(), HttpStatus.OK);
    }

    @GetMapping("/stats/{tenantId}")
    public ResponseEntity<?> getStats(@PathVariable String tenantId) {
        return new ResponseEntity<>(auditClient.getAuditsStats(tenantId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List> search(@RequestParam String tenantId,
                                       @RequestParam String filter,
                                       @RequestParam String sortBy,
                                       @RequestParam String sortDirection,
                                       @RequestParam int offset,
                                       @RequestParam int count) {
        AuditFilter auditFilter = new AuditFilter();
        auditFilter.setTenantId(tenantId);
        auditFilter.setCount(count);
        auditFilter.setOffset(offset);
        auditFilter.setFilter(filter);
        auditFilter.setSortBy(sortBy);
        auditFilter.setSortDirection(sortDirection);
        return new ResponseEntity<>(auditClient.getAudits(auditFilter), HttpStatus.OK);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<?> exportCsv(@RequestParam String tenantId,
                                           @RequestParam String filter,
                                           @RequestParam String sortBy,
                                           @RequestParam String sortDirection,
                                           @RequestParam int offset,
                                           @RequestParam int count) {
        AuditFilter auditFilter = new AuditFilter();
        auditFilter.setTenantId(tenantId);
        auditFilter.setCount(count);
        auditFilter.setOffset(offset);
        auditFilter.setFilter(filter);
        auditFilter.setSortBy(sortBy);
        auditFilter.setSortDirection(sortDirection);
        return new ResponseEntity<>(auditClient.exportCsv(auditFilter, new String[]{}), HttpStatus.OK);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<?> exportPdf(@RequestParam String tenantId,
                                       @RequestParam String filter,
                                       @RequestParam String sortBy,
                                       @RequestParam String sortDirection,
                                       @RequestParam int offset,
                                       @RequestParam int count) {
        AuditFilter auditFilter = new AuditFilter();
        auditFilter.setTenantId(tenantId);
        auditFilter.setCount(count);
        auditFilter.setOffset(offset);
        auditFilter.setFilter(filter);
        auditFilter.setSortBy(sortBy);
        auditFilter.setSortDirection(sortDirection);
        return new ResponseEntity<>(auditClient.exportPdf(auditFilter, new String[]{}), HttpStatus.OK);
    }
}
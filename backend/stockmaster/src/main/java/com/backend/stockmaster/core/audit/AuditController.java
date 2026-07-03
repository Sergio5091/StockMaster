package com.backend.stockmaster.core.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/logs")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'AUDITEUR')")
    public ResponseEntity<Page<AuditLog>> getAllAuditLogs(Pageable pageable) {
        return ResponseEntity.ok(auditService.getAllAuditLogs(pageable));
    }

    @GetMapping("/logs/user/{userName}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'AUDITEUR')")
    public ResponseEntity<Page<AuditLog>> getAuditLogsByUser(
            @PathVariable String userName,
            Pageable pageable) {
        return ResponseEntity.ok(auditService.getAuditLogsByUser(userName, pageable));
    }

    @GetMapping("/logs/entity/{entityName}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'AUDITEUR')")
    public ResponseEntity<Page<AuditLog>> getAuditLogsByEntity(
            @PathVariable String entityName,
            Pageable pageable) {
        return ResponseEntity.ok(auditService.getAuditLogsByEntity(entityName, pageable));
    }

    @GetMapping("/logs/date-range")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'AUDITEUR')")
    public ResponseEntity<Page<AuditLog>> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Pageable pageable) {
        return ResponseEntity.ok(auditService.getAuditLogsByDateRange(start, end, pageable));
    }

    @GetMapping("/logs/count")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'AUDITEUR')")
    public ResponseEntity<Long> countAuditLogs() {
        return ResponseEntity.ok(auditService.countAuditLogs());
    }
}

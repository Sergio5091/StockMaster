package com.backend.stockmaster.core.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public Page<AuditLog> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    public Page<AuditLog> getAuditLogsByUser(String userName, Pageable pageable) {
        List<AuditLog> logs = auditLogRepository.findByPerformedByOrderByPerformedAtDesc(userName);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), logs.size());
        List<AuditLog> pageContent = logs.subList(start, end);
        return new PageImpl<>(pageContent, pageable, logs.size());
    }

    public Page<AuditLog> getAuditLogsByEntity(String entityName, Pageable pageable) {
        List<AuditLog> logs = auditLogRepository.findByEntityNameOrderByPerformedAtDesc(entityName);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), logs.size());
        List<AuditLog> pageContent = logs.subList(start, end);
        return new PageImpl<>(pageContent, pageable, logs.size());
    }

    public Page<AuditLog> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        List<AuditLog> logs = auditLogRepository.findByPerformedAtBetweenOrderByPerformedAtDesc(start, end);
        int pageStart = (int) pageable.getOffset();
        int pageEnd = Math.min((pageStart + pageable.getPageSize()), logs.size());
        List<AuditLog> pageContent = logs.subList(pageStart, pageEnd);
        return new PageImpl<>(pageContent, pageable, logs.size());
    }

    public long countAuditLogs() {
        return auditLogRepository.count();
    }
}

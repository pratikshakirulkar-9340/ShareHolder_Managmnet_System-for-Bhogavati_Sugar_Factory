package com.shms.service;

import com.shms.dto.AuditLogResponse;
import com.shms.entity.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    void log(String username, AuditAction action, String entityName, Long entityId, String description);
    Page<AuditLogResponse> getAll(Pageable pageable);
}

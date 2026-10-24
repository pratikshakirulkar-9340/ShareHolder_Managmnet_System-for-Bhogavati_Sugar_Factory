package com.shms.mapper;

import com.shms.dto.AuditLogResponse;
import com.shms.entity.AuditLog;

public final class AuditLogMapper {

    private AuditLogMapper() {}

    public static AuditLogResponse toResponse(AuditLog a) {
        return AuditLogResponse.builder()
                .id(a.getId())
                .username(a.getUsername())
                .action(a.getAction().name())
                .entityName(a.getEntityName())
                .entityId(a.getEntityId())
                .description(a.getDescription())
                .timestamp(a.getCreatedAt())
                .build();
    }
}

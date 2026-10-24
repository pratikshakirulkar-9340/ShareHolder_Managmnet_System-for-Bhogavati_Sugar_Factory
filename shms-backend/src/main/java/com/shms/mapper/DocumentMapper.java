package com.shms.mapper;

import com.shms.dto.DocumentResponse;
import com.shms.entity.Document;

public final class DocumentMapper {

    private DocumentMapper() {}

    public static DocumentResponse toResponse(Document d) {
        return DocumentResponse.builder()
                .id(d.getId())
                .memberId(d.getShareholder().getMemberId())
                .documentType(d.getDocumentType().name())
                .fileName(d.getFileName())
                .fileSize(d.getFileSize())
                .contentType(d.getContentType())
                .downloadUrl("/api/documents/" + d.getId() + "/download")
                .build();
    }
}

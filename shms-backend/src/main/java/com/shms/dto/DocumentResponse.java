package com.shms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private Long id;
    private String memberId;
    private String documentType;
    private String fileName;
    private Long fileSize;
    private String contentType;
    private String downloadUrl;
}

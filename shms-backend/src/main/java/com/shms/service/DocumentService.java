package com.shms.service;

import com.shms.dto.DocumentResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    DocumentResponse upload(String memberId, String documentType, MultipartFile file, String actorUsername);
    List<DocumentResponse> getByMemberId(String memberId);
    Resource download(Long documentId);
    String getFileName(Long documentId);
    void delete(Long documentId, String actorUsername);
}

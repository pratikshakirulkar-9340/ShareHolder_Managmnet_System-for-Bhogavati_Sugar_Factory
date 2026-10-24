package com.shms.controller;

import com.shms.dto.ApiResponse;
import com.shms.dto.DocumentResponse;
import com.shms.service.DocumentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Tag(name = "Documents")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DocumentResponse> upload(@RequestParam String memberId,
                                                 @RequestParam String documentType,
                                                 @RequestParam("file") MultipartFile file,
                                                 Authentication auth) {
        return ApiResponse.success("Document uploaded", documentService.upload(memberId, documentType, file, auth.getName()));
    }

    @GetMapping("/member/{memberId}")
    public ApiResponse<List<DocumentResponse>> byMember(@PathVariable String memberId) {
        return ApiResponse.success(documentService.getByMemberId(memberId));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Resource resource = documentService.download(id);
        String fileName = documentService.getFileName(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<Void> delete(@PathVariable Long id, Authentication auth) {
        documentService.delete(id, auth.getName());
        return ApiResponse.message("Document deleted");
    }
}

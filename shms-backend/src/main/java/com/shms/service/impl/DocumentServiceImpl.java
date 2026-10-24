package com.shms.service.impl;

import com.shms.dto.DocumentResponse;
import com.shms.entity.Document;
import com.shms.entity.Shareholder;
import com.shms.entity.enums.AuditAction;
import com.shms.entity.enums.DocumentType;
import com.shms.exception.BadRequestException;
import com.shms.exception.ResourceNotFoundException;
import com.shms.mapper.DocumentMapper;
import com.shms.repository.DocumentRepository;
import com.shms.repository.ShareholderRepository;
import com.shms.repository.UserRepository;
import com.shms.service.AuditLogService;
import com.shms.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf", "image/jpeg", "image/png", "image/jpg");
    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024; // 10 MB, matches application.yml

    private final DocumentRepository documentRepository;
    private final ShareholderRepository shareholderRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Value("${app.file-storage.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public DocumentResponse upload(String memberId, String documentType, MultipartFile file, String actorUsername) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("No file was provided");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BadRequestException("File exceeds the 10MB limit");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Unsupported file type: " + file.getContentType() +
                    ". Allowed: PDF, JPEG, PNG");
        }

        Shareholder shareholder = shareholderRepository.findByMemberId(memberId)
                .orElseThrow(() -> ResourceNotFoundException.of("Shareholder", memberId));

        DocumentType type = parseDocumentType(documentType);

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String originalName = sanitizeFileName(file.getOriginalFilename());
            String storedName = UUID.randomUUID() + "_" + originalName;
            Path target = uploadPath.resolve(storedName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            Document document = new Document();
            document.setShareholder(shareholder);
            document.setDocumentType(type);
            document.setFileName(originalName);
            document.setFilePath(target.toString());
            document.setFileSize(file.getSize());
            document.setContentType(file.getContentType());
            userRepository.findByUsername(actorUsername).ifPresent(u -> document.setUploadedBy(u.getId()));

            Document saved = documentRepository.save(document);
            auditLogService.log(actorUsername, AuditAction.DOCUMENT_UPLOADED, "Document", saved.getId(),
                    type + " uploaded for " + memberId);

            return DocumentMapper.toResponse(saved);
        } catch (IOException e) {
            throw new BadRequestException("Failed to store file: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getByMemberId(String memberId) {
        Shareholder shareholder = shareholderRepository.findByMemberId(memberId)
                .orElseThrow(() -> ResourceNotFoundException.of("Shareholder", memberId));
        return documentRepository.findByShareholderId(shareholder.getId()).stream()
                .map(DocumentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Resource download(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> ResourceNotFoundException.of("Document", documentId));
        try {
            Path path = Paths.get(document.getFilePath());
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("Stored file is missing on disk for document " + documentId);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Invalid file path for document " + documentId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getFileName(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> ResourceNotFoundException.of("Document", documentId))
                .getFileName();
    }

    @Override
    @Transactional
    public void delete(Long documentId, String actorUsername) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> ResourceNotFoundException.of("Document", documentId));
        try {
            Files.deleteIfExists(Paths.get(document.getFilePath()));
        } catch (IOException ignored) {
            // File already gone or locked - proceed to remove the DB record regardless.
        }
        documentRepository.delete(document);
        auditLogService.log(actorUsername, AuditAction.DOCUMENT_DELETED, "Document", documentId,
                "Deleted document " + document.getFileName());
    }

    private DocumentType parseDocumentType(String type) {
        try {
            return DocumentType.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BadRequestException("Invalid documentType: " + type);
        }
    }

    private String sanitizeFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "file";
        }
        return originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}

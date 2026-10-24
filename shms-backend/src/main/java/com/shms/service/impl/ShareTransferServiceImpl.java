package com.shms.service.impl;

import com.shms.dto.TransferDecisionRequest;
import com.shms.dto.TransferRequest;
import com.shms.dto.TransferResponse;
import com.shms.entity.ShareTransfer;
import com.shms.entity.Shareholder;
import com.shms.entity.enums.AuditAction;
import com.shms.entity.enums.ShareholderStatus;
import com.shms.entity.enums.TransferReason;
import com.shms.entity.enums.TransferStatus;
import com.shms.exception.BadRequestException;
import com.shms.exception.ResourceNotFoundException;
import com.shms.mapper.TransferMapper;
import com.shms.repository.ShareTransferRepository;
import com.shms.repository.ShareholderRepository;
import com.shms.repository.UserRepository;
import com.shms.service.AuditLogService;
import com.shms.service.ShareTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PENDING -> ADMIN reviews -> APPROVED/REJECTED workflow, added on top of the legacy app
 * (which performed a direct, unchecked insert). Supports DEATH/SALE/GIFT as reason types,
 * not just death, per the agreed scope in the Phase-0 open questions.
 */
@Service
@RequiredArgsConstructor
public class ShareTransferServiceImpl implements ShareTransferService {

    private final ShareTransferRepository transferRepository;
    private final ShareholderRepository shareholderRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public TransferResponse request(TransferRequest request, String actorUsername) {
        Shareholder from = shareholderRepository.findByMemberId(request.getFromMemberId())
                .orElseThrow(() -> ResourceNotFoundException.of("Shareholder", request.getFromMemberId()));

        Shareholder to = null;
        if (StringUtils.hasText(request.getToMemberId())) {
            to = shareholderRepository.findByMemberId(request.getToMemberId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Shareholder", request.getToMemberId()));
        }

        ShareTransfer transfer = new ShareTransfer();
        transfer.setFromShareholder(from);
        transfer.setToShareholder(to);
        transfer.setNomineeName(request.getNomineeName());
        transfer.setReasonType(parseReason(request.getReasonType()));
        transfer.setReasonDetail(request.getReasonDetail());
        transfer.setTransferDate(request.getTransferDate());
        transfer.setStatus(TransferStatus.PENDING);
        userRepository.findByUsername(actorUsername).ifPresent(u -> transfer.setRequestedBy(u.getId()));

        ShareTransfer saved = transferRepository.save(transfer);
        auditLogService.log(actorUsername, AuditAction.TRANSFER_REQUEST, "ShareTransfer", saved.getId(),
                "Transfer requested for " + from.getMemberId() + " (" + transfer.getReasonType() + ")");

        return TransferMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TransferResponse decide(Long transferId, TransferDecisionRequest decision, String actorUsername) {
        ShareTransfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> ResourceNotFoundException.of("ShareTransfer", transferId));

        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new BadRequestException("Transfer " + transferId + " has already been " + transfer.getStatus());
        }

        String decisionValue = decision.getDecision() == null ? "" : decision.getDecision().trim().toUpperCase();
        userRepository.findByUsername(actorUsername).ifPresent(u -> transfer.setReviewedBy(u.getId()));
        transfer.setReviewedAt(Instant.now());

        if ("APPROVED".equals(decisionValue)) {
            transfer.setStatus(TransferStatus.APPROVED);
            Shareholder from = transfer.getFromShareholder();
            from.setStatus(ShareholderStatus.TRANSFERRED);
            shareholderRepository.save(from);

            if (transfer.getToShareholder() != null) {
                Shareholder to = transfer.getToShareholder();
                to.setShareClass(from.getShareClass());
                to.setStatus(ShareholderStatus.ACTIVE);
                shareholderRepository.save(to);
            }

            auditLogService.log(actorUsername, AuditAction.TRANSFER_APPROVED, "ShareTransfer", transfer.getId(),
                    "Transfer approved for " + from.getMemberId());

        } else if ("REJECTED".equals(decisionValue)) {
            transfer.setStatus(TransferStatus.REJECTED);
            auditLogService.log(actorUsername, AuditAction.TRANSFER_REJECTED, "ShareTransfer", transfer.getId(),
                    "Transfer rejected for " + transfer.getFromShareholder().getMemberId() +
                            (StringUtils.hasText(decision.getRemarks()) ? ": " + decision.getRemarks() : ""));
        } else {
            throw new BadRequestException("decision must be APPROVED or REJECTED, got: " + decision.getDecision());
        }

        ShareTransfer saved = transferRepository.save(transfer);
        return TransferMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferResponse> getAll() {
        return transferRepository.findAll().stream().map(TransferMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferResponse> getPending() {
        return transferRepository.findByStatus(TransferStatus.PENDING).stream()
                .map(TransferMapper::toResponse)
                .collect(Collectors.toList());
    }

    private TransferReason parseReason(String reason) {
        try {
            return TransferReason.valueOf(reason.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BadRequestException("reasonType must be DEATH, SALE, or GIFT, got: " + reason);
        }
    }
}

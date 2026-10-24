package com.shms.service.impl;

import com.shms.dto.AllotmentRequest;
import com.shms.dto.AllotmentResponse;
import com.shms.entity.Rate;
import com.shms.entity.ShareAllotment;
import com.shms.entity.Shareholder;
import com.shms.entity.enums.AuditAction;
import com.shms.entity.enums.ShareClass;
import com.shms.exception.BadRequestException;
import com.shms.exception.ResourceNotFoundException;
import com.shms.mapper.AllotmentMapper;
import com.shms.repository.ShareAllotmentRepository;
import com.shms.repository.ShareholderRepository;
import com.shms.service.AuditLogService;
import com.shms.service.RateService;
import com.shms.service.ShareAllotmentService;
import com.shms.util.MemberIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements the classification rule preserved from the legacy .NET app:
 * land ownership + verified evidence => Class A (share purchase + monthly sugar quota).
 * No land / no membership => Class C (amounts zeroed; entitlement instead comes from
 * cane tonnage, handled by TonnesSugarService).
 */
@Service
@RequiredArgsConstructor
public class ShareAllotmentServiceImpl implements ShareAllotmentService {

    private final ShareAllotmentRepository allotmentRepository;
    private final ShareholderRepository shareholderRepository;
    private final RateService rateService;
    private final MemberIdGenerator memberIdGenerator;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public AllotmentResponse allot(AllotmentRequest request, String actorUsername) {
        Shareholder shareholder = shareholderRepository.findByMemberId(request.getMemberId())
                .orElseThrow(() -> ResourceNotFoundException.of("Shareholder", request.getMemberId()));

        if (shareholder.getShareClass() != ShareClass.UNASSIGNED) {
            throw new BadRequestException(
                    "Shareholder " + request.getMemberId() + " has already been allotted a class (" +
                            shareholder.getShareClass() + "). Use the transfer workflow to change ownership instead.");
        }

        Rate rate = rateService.resolveEffectiveRate(request.getPurchasingDate());
        boolean isClassA = request.isLandOwner() && request.isEvidenceVerified();
        ShareClass shareClass = isClassA ? ShareClass.A : ShareClass.C;

        ShareAllotment allotment = new ShareAllotment();
        allotment.setShareholder(shareholder);
        allotment.setRate(rate);
        allotment.setPurchasingDate(request.getPurchasingDate());
        allotment.setTypeOfShare(shareClass);
        allotment.setPlantingDate(request.getPlantingDate());
        allotment.setTypeOfSugarcane(request.getTypeOfSugarcane());
        allotment.setNomineeName(request.getNomineeName());
        allotment.setDirectorName(request.getDirectorName());

        if (isClassA) {
            allotment.setSharePurchaseAmount(rate.getSharePurchaseAmount());
            allotment.setPerMonthSugar(rate.getPerMonthShareSugar());
        } else {
            allotment.setSharePurchaseAmount(BigDecimal.ZERO);
            allotment.setPerMonthSugar(BigDecimal.ZERO);
        }

        ShareAllotment saved = allotmentRepository.save(allotment);

        // Reassign the human-facing member ID with the correct class suffix now that class is known.
        shareholder.setShareClass(shareClass);
        shareholder.setMemberId(memberIdGenerator.generateForClass(shareClass));
        shareholderRepository.save(shareholder);

        auditLogService.log(actorUsername, AuditAction.CREATE_ALLOTMENT, "ShareAllotment", saved.getId(),
                "Allotted " + shareholder.getMemberId() + " as Class " + shareClass);

        return AllotmentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllotmentResponse> getByMemberId(String memberId) {
        Shareholder shareholder = shareholderRepository.findByMemberId(memberId)
                .orElseThrow(() -> ResourceNotFoundException.of("Shareholder", memberId));
        return allotmentRepository.findByShareholderId(shareholder.getId()).stream()
                .map(AllotmentMapper::toResponse)
                .collect(Collectors.toList());
    }
}

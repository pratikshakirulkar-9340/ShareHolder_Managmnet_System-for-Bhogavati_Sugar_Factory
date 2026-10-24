package com.shms.service.impl;

import com.shms.dto.ShareholderRequest;
import com.shms.dto.ShareholderResponse;
import com.shms.entity.Shareholder;
import com.shms.entity.enums.AuditAction;
import com.shms.entity.enums.ShareClass;
import com.shms.entity.enums.ShareholderStatus;
import com.shms.exception.ResourceNotFoundException;
import com.shms.mapper.ShareholderMapper;
import com.shms.repository.ShareholderRepository;
import com.shms.service.AuditLogService;
import com.shms.service.ShareholderService;
import com.shms.specification.ShareholderSpecification;
import com.shms.util.MemberIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ShareholderServiceImpl implements ShareholderService {

    private final ShareholderRepository shareholderRepository;
    private final MemberIdGenerator memberIdGenerator;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public ShareholderResponse register(ShareholderRequest request, String actorUsername) {
        Shareholder shareholder = new Shareholder();
        applyRequest(shareholder, request);
        shareholder.setShareClass(ShareClass.UNASSIGNED);
        shareholder.setStatus(ShareholderStatus.ACTIVE);
        shareholder.setMemberId(memberIdGenerator.generateForClass(ShareClass.UNASSIGNED));

        Shareholder saved = shareholderRepository.save(shareholder);
        auditLogService.log(actorUsername, AuditAction.CREATE_SHAREHOLDER, "Shareholder", saved.getId(),
                "Registered shareholder " + saved.getMemberId() + " (" + saved.getMemberName() + ")");

        return ShareholderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ShareholderResponse getByMemberId(String memberId) {
        return ShareholderMapper.toResponse(findByMemberIdOrThrow(memberId));
    }

    @Override
    @Transactional
    public ShareholderResponse update(String memberId, ShareholderRequest request, String actorUsername) {
        Shareholder shareholder = findByMemberIdOrThrow(memberId);
        applyRequest(shareholder, request);

        Shareholder saved = shareholderRepository.save(shareholder);
        auditLogService.log(actorUsername, AuditAction.UPDATE_SHAREHOLDER, "Shareholder", saved.getId(),
                "Updated shareholder " + saved.getMemberId());

        return ShareholderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(String memberId, String actorUsername) {
        Shareholder shareholder = findByMemberIdOrThrow(memberId);
        shareholderRepository.delete(shareholder);
        auditLogService.log(actorUsername, AuditAction.DELETE_SHAREHOLDER, "Shareholder", shareholder.getId(),
                "Deleted shareholder " + memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShareholderResponse> search(String memberId, String name, String village, String shareClass,
                                             String status, Pageable pageable) {
        ShareClass classFilter = StringUtils.hasText(shareClass) ? ShareClass.valueOf(shareClass.toUpperCase()) : null;
        ShareholderStatus statusFilter = StringUtils.hasText(status) ? ShareholderStatus.valueOf(status.toUpperCase()) : null;

        return shareholderRepository
                .findAll(ShareholderSpecification.search(memberId, name, village, classFilter, statusFilter), pageable)
                .map(ShareholderMapper::toResponse);
    }

    private void applyRequest(Shareholder shareholder, ShareholderRequest request) {
        shareholder.setMemberName(request.getMemberName());
        shareholder.setTaluka(request.getTaluka());
        shareholder.setDistrict(request.getDistrict());
        shareholder.setVillage(request.getVillage());
        shareholder.setGatNo(request.getGatNo());
        shareholder.setAge(request.getAge());
        shareholder.setFarmerLand(request.getFarmerLand());
        shareholder.setContactNo(request.getContactNo());
        shareholder.setGender(request.getGender());
        shareholder.setRegistrationDate(request.getRegistrationDate());
        shareholder.setNomineeName(request.getNomineeName());
        shareholder.setDirectorName(request.getDirectorName());
    }

    private Shareholder findByMemberIdOrThrow(String memberId) {
        return shareholderRepository.findByMemberId(memberId)
                .orElseThrow(() -> ResourceNotFoundException.of("Shareholder", memberId));
    }
}

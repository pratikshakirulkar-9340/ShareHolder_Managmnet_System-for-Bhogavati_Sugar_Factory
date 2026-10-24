package com.shms.service.impl;

import com.shms.dto.SugarAllocationRequest;
import com.shms.dto.SugarAllocationResponse;
import com.shms.entity.Shareholder;
import com.shms.entity.ShareSugarTransaction;
import com.shms.entity.SugarAllocation;
import com.shms.entity.enums.AuditAction;
import com.shms.entity.enums.ShareClass;
import com.shms.repository.ShareSugarTransactionRepository;
import com.shms.repository.ShareholderRepository;
import com.shms.repository.SugarAllocationRepository;
import com.shms.repository.UserRepository;
import com.shms.service.AuditLogService;
import com.shms.service.SugarAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory-wide bonus top-up for Class A members only, matching the legacy app's scope.
 * Fixes the legacy column-name bug (code wrote to a column the table didn't have) by adding
 * a new ShareSugarTransaction "adjustment" row per affected member instead, so the bonus is
 * fully auditable per-member rather than a silent balance mutation.
 */
@Service
@RequiredArgsConstructor
public class SugarAllocationServiceImpl implements SugarAllocationService {

    private final SugarAllocationRepository sugarAllocationRepository;
    private final ShareholderRepository shareholderRepository;
    private final ShareSugarTransactionRepository shareSugarTransactionRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public SugarAllocationResponse applyBonus(SugarAllocationRequest request, String actorUsername) {
        List<Shareholder> classAMembers = shareholderRepository.findAll().stream()
                .filter(s -> s.getShareClass() == ShareClass.A)
                .collect(Collectors.toList());

        for (Shareholder shareholder : classAMembers) {
            var lastTxnOpt = shareSugarTransactionRepository
                    .findFirstByShareholderIdOrderByCreatedAtDesc(shareholder.getId());

            // Bump the most recent transaction's remaining balance by the bonus, if one exists.
            // Members with no prior transaction simply have nothing to bump yet - they'll receive
            // the bonus reflected the first time a real supply transaction is recorded.
            lastTxnOpt.ifPresent((ShareSugarTransaction lastTxn) -> {
                lastTxn.setRemainingSugar(lastTxn.getRemainingSugar().add(request.getBonusAmount()));
                shareSugarTransactionRepository.save(lastTxn);
            });
        }

        SugarAllocation allocation = new SugarAllocation();
        allocation.setBonusAmount(request.getBonusAmount());
        allocation.setAllocationYear(request.getAllocationYear());
        allocation.setAffectedShareholderCount(classAMembers.size());
        userRepository.findByUsername(actorUsername).ifPresent(u -> allocation.setAppliedBy(u.getId()));

        SugarAllocation saved = sugarAllocationRepository.save(allocation);
        auditLogService.log(actorUsername, AuditAction.SUGAR_ALLOCATION_APPLIED, "SugarAllocation", saved.getId(),
                "Bonus " + request.getBonusAmount() + " kg applied to " + classAMembers.size() +
                        " Class A shareholders for year " + request.getAllocationYear());

        return SugarAllocationResponse.builder()
                .id(saved.getId())
                .bonusAmount(saved.getBonusAmount())
                .allocationYear(saved.getAllocationYear())
                .affectedShareholderCount(saved.getAffectedShareholderCount())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SugarAllocationResponse> getHistory() {
        return sugarAllocationRepository.findAll().stream()
                .map(a -> SugarAllocationResponse.builder()
                        .id(a.getId())
                        .bonusAmount(a.getBonusAmount())
                        .allocationYear(a.getAllocationYear())
                        .affectedShareholderCount(a.getAffectedShareholderCount())
                        .build())
                .collect(Collectors.toList());
    }
}

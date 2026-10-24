package com.shms.service.impl;

import com.shms.dto.ShareSugarRequest;
import com.shms.dto.ShareSugarResponse;
import com.shms.entity.Rate;
import com.shms.entity.ShareSugarTransaction;
import com.shms.entity.Shareholder;
import com.shms.entity.enums.AuditAction;
import com.shms.entity.enums.ShareClass;
import com.shms.exception.BadRequestException;
import com.shms.exception.ResourceNotFoundException;
import com.shms.mapper.ShareSugarMapper;
import com.shms.repository.ShareSugarTransactionRepository;
import com.shms.repository.ShareholderRepository;
import com.shms.service.AuditLogService;
import com.shms.service.RateService;
import com.shms.service.ShareSugarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class A entitlement math (bug-fixed vs. the legacy app - see Phase-0 analysis open question #2):
 *   entitlement       = perMonthSugar * 12               (this period's fresh annual allocation)
 *   remainingSugar    = previousRemaining + entitlement - suppliedSugar
 *   totalAmount       = suppliedSugar * ratePerKg
 */
@Service
@RequiredArgsConstructor
public class ShareSugarServiceImpl implements ShareSugarService {

    private final ShareSugarTransactionRepository transactionRepository;
    private final ShareholderRepository shareholderRepository;
    private final RateService rateService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public ShareSugarResponse recordSupply(ShareSugarRequest request, String actorUsername) {
        Shareholder shareholder = shareholderRepository.findByMemberId(request.getMemberId())
                .orElseThrow(() -> ResourceNotFoundException.of("Shareholder", request.getMemberId()));

        if (shareholder.getShareClass() != ShareClass.A) {
            throw new BadRequestException(
                    request.getMemberId() + " is not a Class A shareholder - use the Tonnes Sugar endpoint instead");
        }

        Rate rate = rateService.resolveEffectiveRate(request.getSuppliedDate());

        BigDecimal previousRemaining = transactionRepository
                .findFirstByShareholderIdOrderByCreatedAtDesc(shareholder.getId())
                .map(ShareSugarTransaction::getRemainingSugar)
                .orElse(BigDecimal.ZERO);

        BigDecimal entitlement = rate.getPerMonthShareSugar().multiply(BigDecimal.valueOf(12));
        BigDecimal remaining = previousRemaining.add(entitlement).subtract(request.getSuppliedSugar());

        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Supplied sugar (" + request.getSuppliedSugar() +
                    " kg) exceeds available entitlement (" + previousRemaining.add(entitlement) + " kg)");
        }

        ShareSugarTransaction txn = new ShareSugarTransaction();
        txn.setShareholder(shareholder);
        txn.setRate(rate);
        txn.setEntitlement(entitlement);
        txn.setSuppliedSugar(request.getSuppliedSugar());
        txn.setPreviousRemaining(previousRemaining);
        txn.setRemainingSugar(remaining);
        txn.setRatePerKg(rate.getRateOfShareSugar());
        txn.setTotalAmount(request.getSuppliedSugar().multiply(rate.getRateOfShareSugar()));
        txn.setSuppliedDate(request.getSuppliedDate());

        ShareSugarTransaction saved = transactionRepository.save(txn);
        auditLogService.log(actorUsername, AuditAction.CREATE_SHARE_SUGAR_TXN, "ShareSugarTransaction", saved.getId(),
                request.getSuppliedSugar() + " kg supplied by " + shareholder.getMemberId());

        return ShareSugarMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShareSugarResponse> getByMemberId(String memberId) {
        Shareholder shareholder = shareholderRepository.findByMemberId(memberId)
                .orElseThrow(() -> ResourceNotFoundException.of("Shareholder", memberId));
        return transactionRepository.findByShareholderIdOrderBySuppliedDateDesc(shareholder.getId()).stream()
                .map(ShareSugarMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShareSugarResponse> getByDateRange(LocalDate from, LocalDate to) {
        return transactionRepository.findBySuppliedDateBetween(from, to).stream()
                .map(ShareSugarMapper::toResponse)
                .collect(Collectors.toList());
    }
}

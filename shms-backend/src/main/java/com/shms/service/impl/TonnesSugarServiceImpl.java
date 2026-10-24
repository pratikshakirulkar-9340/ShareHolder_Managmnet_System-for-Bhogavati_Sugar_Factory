package com.shms.service.impl;

import com.shms.dto.TonnesSugarRequest;
import com.shms.dto.TonnesSugarResponse;
import com.shms.entity.Rate;
import com.shms.entity.Shareholder;
import com.shms.entity.TonnesSugarTransaction;
import com.shms.entity.enums.AuditAction;
import com.shms.entity.enums.ShareClass;
import com.shms.exception.BadRequestException;
import com.shms.exception.ResourceNotFoundException;
import com.shms.mapper.TonnesSugarMapper;
import com.shms.repository.ShareholderRepository;
import com.shms.repository.TonnesSugarTransactionRepository;
import com.shms.service.AuditLogService;
import com.shms.service.RateService;
import com.shms.service.TonnesSugarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class C entitlement math (same running-balance pattern as ShareSugarServiceImpl, applied to
 * cane-tonnage instead of a monthly quota):
 *   entitlement    = totalTonnes * 1kg (legacy 1:1 ratio, preserved from the .NET app)
 *   remainingSugar = previousRemaining + entitlement - suppliedSugar
 *   amount         = suppliedSugar * ratePerKg
 */
@Service
@RequiredArgsConstructor
public class TonnesSugarServiceImpl implements TonnesSugarService {

    private final TonnesSugarTransactionRepository transactionRepository;
    private final ShareholderRepository shareholderRepository;
    private final RateService rateService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public TonnesSugarResponse recordSupply(TonnesSugarRequest request, String actorUsername) {
        Shareholder shareholder = shareholderRepository.findByMemberId(request.getMemberId())
                .orElseThrow(() -> ResourceNotFoundException.of("Shareholder", request.getMemberId()));

        if (shareholder.getShareClass() != ShareClass.C) {
            throw new BadRequestException(
                    request.getMemberId() + " is not a Class C shareholder - use the Share Sugar endpoint instead");
        }

        Rate rate = rateService.resolveEffectiveRate(request.getSuppliedDate());

        BigDecimal previousRemaining = transactionRepository
                .findFirstByShareholderIdOrderByCreatedAtDesc(shareholder.getId())
                .map(TonnesSugarTransaction::getRemainingSugar)
                .orElse(BigDecimal.ZERO);

        BigDecimal entitlement = request.getTotalTonnes(); // 1kg sugar per unit tonnage, per legacy ratio
        BigDecimal remaining = previousRemaining.add(entitlement).subtract(request.getSuppliedSugar());

        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Supplied sugar (" + request.getSuppliedSugar() +
                    " kg) exceeds available entitlement (" + previousRemaining.add(entitlement) + " kg)");
        }

        TonnesSugarTransaction txn = new TonnesSugarTransaction();
        txn.setShareholder(shareholder);
        txn.setRate(rate);
        txn.setTotalTonnes(request.getTotalTonnes());
        txn.setEntitlement(entitlement);
        txn.setSuppliedSugar(request.getSuppliedSugar());
        txn.setPreviousRemaining(previousRemaining);
        txn.setRemainingSugar(remaining);
        txn.setRatePerKg(rate.getRateOfTonnesSugar());
        txn.setAmount(request.getSuppliedSugar().multiply(rate.getRateOfTonnesSugar()));
        txn.setSuppliedDate(request.getSuppliedDate());

        TonnesSugarTransaction saved = transactionRepository.save(txn);
        auditLogService.log(actorUsername, AuditAction.CREATE_TONNES_SUGAR_TXN, "TonnesSugarTransaction", saved.getId(),
                request.getSuppliedSugar() + " kg supplied by " + shareholder.getMemberId());

        return TonnesSugarMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TonnesSugarResponse> getByMemberId(String memberId) {
        Shareholder shareholder = shareholderRepository.findByMemberId(memberId)
                .orElseThrow(() -> ResourceNotFoundException.of("Shareholder", memberId));
        return transactionRepository.findByShareholderIdOrderBySuppliedDateDesc(shareholder.getId()).stream()
                .map(TonnesSugarMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TonnesSugarResponse> getByDateRange(LocalDate from, LocalDate to) {
        return transactionRepository.findBySuppliedDateBetween(from, to).stream()
                .map(TonnesSugarMapper::toResponse)
                .collect(Collectors.toList());
    }
}

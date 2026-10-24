package com.shms.service.impl;

import com.shms.dto.DashboardSummaryResponse;
import com.shms.entity.enums.ShareClass;
import com.shms.entity.enums.TransferStatus;
import com.shms.repository.ShareSugarTransactionRepository;
import com.shms.repository.ShareTransferRepository;
import com.shms.repository.ShareholderRepository;
import com.shms.repository.TonnesSugarTransactionRepository;
import com.shms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ShareholderRepository shareholderRepository;
    private final ShareSugarTransactionRepository shareSugarTransactionRepository;
    private final TonnesSugarTransactionRepository tonnesSugarTransactionRepository;
    private final ShareTransferRepository shareTransferRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        BigDecimal totalShareSugar = shareSugarTransactionRepository.findAll().stream()
                .map(t -> t.getSuppliedSugar())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTonnesSugar = tonnesSugarTransactionRepository.findAll().stream()
                .map(t -> t.getSuppliedSugar())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardSummaryResponse.builder()
                .totalShareholders(shareholderRepository.count())
                .classAShareholders(shareholderRepository.countByShareClass(ShareClass.A))
                .classCShareholders(shareholderRepository.countByShareClass(ShareClass.C))
                .totalShareSugarSupplied(totalShareSugar)
                .totalTonnesSugarSupplied(totalTonnesSugar)
                .pendingTransfers(shareTransferRepository.countByStatus(TransferStatus.PENDING))
                .approvedTransfers(shareTransferRepository.countByStatus(TransferStatus.APPROVED))
                .rejectedTransfers(shareTransferRepository.countByStatus(TransferStatus.REJECTED))
                .build();
    }
}

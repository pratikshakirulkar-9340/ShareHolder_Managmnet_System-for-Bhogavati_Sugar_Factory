package com.shms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private long totalShareholders;
    private long classAShareholders;
    private long classCShareholders;
    private BigDecimal totalShareSugarSupplied;
    private BigDecimal totalTonnesSugarSupplied;
    private long pendingTransfers;
    private long approvedTransfers;
    private long rejectedTransfers;
}

package com.shms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareSugarResponse {
    private Long id;
    private String memberId;
    private String memberName;
    private BigDecimal entitlement;
    private BigDecimal suppliedSugar;
    private BigDecimal previousRemaining;
    private BigDecimal remainingSugar;
    private BigDecimal ratePerKg;
    private BigDecimal totalAmount;
    private LocalDate suppliedDate;
}

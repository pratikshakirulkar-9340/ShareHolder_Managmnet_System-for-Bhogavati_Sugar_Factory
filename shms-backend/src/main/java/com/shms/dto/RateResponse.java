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
public class RateResponse {
    private Long id;
    private String sugarFactoryName;
    private BigDecimal sharePurchaseAmount;
    private BigDecimal perMonthShareSugar;
    private BigDecimal rateOfShareSugar;
    private BigDecimal rateOfTonnesSugar;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
}

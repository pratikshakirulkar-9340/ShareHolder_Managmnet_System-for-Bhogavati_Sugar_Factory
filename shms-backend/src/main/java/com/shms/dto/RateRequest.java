package com.shms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RateRequest {

    @NotBlank
    private String sugarFactoryName;

    @NotNull @Positive
    private BigDecimal sharePurchaseAmount;

    @NotNull @Positive
    private BigDecimal perMonthShareSugar;

    @NotNull @Positive
    private BigDecimal rateOfShareSugar;

    @NotNull @Positive
    private BigDecimal rateOfTonnesSugar;

    /** Defaults to today if not supplied. */
    private LocalDate effectiveFrom;
}

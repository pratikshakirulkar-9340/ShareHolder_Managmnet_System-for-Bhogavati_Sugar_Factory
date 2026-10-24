package com.shms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TonnesSugarRequest {

    @NotBlank
    private String memberId;

    @NotNull @PositiveOrZero
    private BigDecimal totalTonnes;

    @NotNull @PositiveOrZero
    private BigDecimal suppliedSugar;

    @NotNull
    private LocalDate suppliedDate;
}

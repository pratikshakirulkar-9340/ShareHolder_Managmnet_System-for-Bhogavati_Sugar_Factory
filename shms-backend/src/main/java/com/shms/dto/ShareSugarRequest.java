package com.shms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ShareSugarRequest {

    @NotBlank
    private String memberId;

    @NotNull @PositiveOrZero
    private java.math.BigDecimal suppliedSugar;

    @NotNull
    private LocalDate suppliedDate;
}

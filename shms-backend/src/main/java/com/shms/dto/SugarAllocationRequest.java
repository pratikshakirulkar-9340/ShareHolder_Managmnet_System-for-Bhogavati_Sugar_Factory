package com.shms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SugarAllocationRequest {

    @NotNull @Positive
    private BigDecimal bonusAmount;

    @NotNull
    private Integer allocationYear;
}

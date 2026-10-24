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
public class SugarAllocationResponse {
    private Long id;
    private BigDecimal bonusAmount;
    private Integer allocationYear;
    private Integer affectedShareholderCount;
}

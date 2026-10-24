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
public class AllotmentResponse {
    private Long id;
    private String memberId;
    private String memberName;
    private BigDecimal sharePurchaseAmount;
    private LocalDate purchasingDate;
    private BigDecimal perMonthSugar;
    private String typeOfShare;
    private LocalDate plantingDate;
    private String typeOfSugarcane;
    private String nomineeName;
    private String directorName;
}

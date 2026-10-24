package com.shms.mapper;

import com.shms.dto.RateResponse;
import com.shms.entity.Rate;

public final class RateMapper {

    private RateMapper() {}

    public static RateResponse toResponse(Rate r) {
        return RateResponse.builder()
                .id(r.getId())
                .sugarFactoryName(r.getSugarFactoryName())
                .sharePurchaseAmount(r.getSharePurchaseAmount())
                .perMonthShareSugar(r.getPerMonthShareSugar())
                .rateOfShareSugar(r.getRateOfShareSugar())
                .rateOfTonnesSugar(r.getRateOfTonnesSugar())
                .effectiveFrom(r.getEffectiveFrom())
                .effectiveTo(r.getEffectiveTo())
                .build();
    }
}

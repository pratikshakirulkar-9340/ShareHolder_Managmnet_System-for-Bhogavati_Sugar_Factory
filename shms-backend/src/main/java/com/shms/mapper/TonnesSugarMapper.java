package com.shms.mapper;

import com.shms.dto.TonnesSugarResponse;
import com.shms.entity.TonnesSugarTransaction;

public final class TonnesSugarMapper {

    private TonnesSugarMapper() {}

    public static TonnesSugarResponse toResponse(TonnesSugarTransaction t) {
        return TonnesSugarResponse.builder()
                .id(t.getId())
                .memberId(t.getShareholder().getMemberId())
                .memberName(t.getShareholder().getMemberName())
                .totalTonnes(t.getTotalTonnes())
                .entitlement(t.getEntitlement())
                .suppliedSugar(t.getSuppliedSugar())
                .previousRemaining(t.getPreviousRemaining())
                .remainingSugar(t.getRemainingSugar())
                .ratePerKg(t.getRatePerKg())
                .amount(t.getAmount())
                .suppliedDate(t.getSuppliedDate())
                .build();
    }
}

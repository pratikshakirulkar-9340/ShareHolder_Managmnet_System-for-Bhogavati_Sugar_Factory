package com.shms.mapper;

import com.shms.dto.ShareSugarResponse;
import com.shms.entity.ShareSugarTransaction;

public final class ShareSugarMapper {

    private ShareSugarMapper() {}

    public static ShareSugarResponse toResponse(ShareSugarTransaction t) {
        return ShareSugarResponse.builder()
                .id(t.getId())
                .memberId(t.getShareholder().getMemberId())
                .memberName(t.getShareholder().getMemberName())
                .entitlement(t.getEntitlement())
                .suppliedSugar(t.getSuppliedSugar())
                .previousRemaining(t.getPreviousRemaining())
                .remainingSugar(t.getRemainingSugar())
                .ratePerKg(t.getRatePerKg())
                .totalAmount(t.getTotalAmount())
                .suppliedDate(t.getSuppliedDate())
                .build();
    }
}

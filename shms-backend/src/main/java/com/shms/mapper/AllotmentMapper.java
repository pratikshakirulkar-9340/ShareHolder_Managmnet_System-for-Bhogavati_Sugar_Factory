package com.shms.mapper;

import com.shms.dto.AllotmentResponse;
import com.shms.entity.ShareAllotment;

public final class AllotmentMapper {

    private AllotmentMapper() {}

    public static AllotmentResponse toResponse(ShareAllotment a) {
        return AllotmentResponse.builder()
                .id(a.getId())
                .memberId(a.getShareholder().getMemberId())
                .memberName(a.getShareholder().getMemberName())
                .sharePurchaseAmount(a.getSharePurchaseAmount())
                .purchasingDate(a.getPurchasingDate())
                .perMonthSugar(a.getPerMonthSugar())
                .typeOfShare(a.getTypeOfShare().name())
                .plantingDate(a.getPlantingDate())
                .typeOfSugarcane(a.getTypeOfSugarcane())
                .nomineeName(a.getNomineeName())
                .directorName(a.getDirectorName())
                .build();
    }
}

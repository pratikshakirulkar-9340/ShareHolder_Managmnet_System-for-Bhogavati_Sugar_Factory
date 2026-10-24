package com.shms.mapper;

import com.shms.dto.TransferResponse;
import com.shms.entity.ShareTransfer;

public final class TransferMapper {

    private TransferMapper() {}

    public static TransferResponse toResponse(ShareTransfer t) {
        return TransferResponse.builder()
                .id(t.getId())
                .fromMemberId(t.getFromShareholder().getMemberId())
                .fromMemberName(t.getFromShareholder().getMemberName())
                .toMemberId(t.getToShareholder() != null ? t.getToShareholder().getMemberId() : null)
                .nomineeName(t.getNomineeName())
                .reasonType(t.getReasonType().name())
                .reasonDetail(t.getReasonDetail())
                .transferDate(t.getTransferDate())
                .status(t.getStatus().name())
                .reviewedAt(t.getReviewedAt())
                .build();
    }
}

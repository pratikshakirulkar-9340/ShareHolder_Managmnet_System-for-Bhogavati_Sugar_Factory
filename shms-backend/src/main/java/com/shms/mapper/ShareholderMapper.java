package com.shms.mapper;

import com.shms.dto.ShareholderResponse;
import com.shms.entity.Shareholder;

public final class ShareholderMapper {

    private ShareholderMapper() {}

    public static ShareholderResponse toResponse(Shareholder s) {
        return ShareholderResponse.builder()
                .id(s.getId())
                .memberId(s.getMemberId())
                .memberName(s.getMemberName())
                .taluka(s.getTaluka())
                .district(s.getDistrict())
                .village(s.getVillage())
                .gatNo(s.getGatNo())
                .age(s.getAge())
                .farmerLand(s.getFarmerLand())
                .contactNo(s.getContactNo())
                .gender(s.getGender())
                .registrationDate(s.getRegistrationDate())
                .nomineeName(s.getNomineeName())
                .directorName(s.getDirectorName())
                .shareClass(s.getShareClass().name())
                .status(s.getStatus().name())
                .build();
    }
}

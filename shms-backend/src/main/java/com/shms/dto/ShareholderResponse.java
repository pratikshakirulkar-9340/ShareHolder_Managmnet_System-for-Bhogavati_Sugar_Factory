package com.shms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareholderResponse {
    private Long id;
    private String memberId;
    private String memberName;
    private String taluka;
    private String district;
    private String village;
    private Integer gatNo;
    private Integer age;
    private String farmerLand;
    private String contactNo;
    private String gender;
    private LocalDate registrationDate;
    private String nomineeName;
    private String directorName;
    private String shareClass;
    private String status;
}

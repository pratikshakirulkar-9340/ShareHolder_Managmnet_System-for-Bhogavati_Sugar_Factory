package com.shms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ShareholderRequest {

    @NotBlank
    private String memberName;

    private String taluka;
    private String district;
    private String village;
    private Integer gatNo;

    @Positive
    private Integer age;

    private String farmerLand;
    private String contactNo;
    private String gender;

    @NotNull
    private LocalDate registrationDate;

    private String nomineeName;
    private String directorName;
}

package com.shms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AllotmentRequest {

    @NotBlank
    private String memberId;

    /** true if the member owns land, false/absent if this is a no-land / no-membership (Class C) allotment. */
    private boolean landOwner;

    /** true if supporting land evidence has been verified. Both this and landOwner true => Class A. */
    private boolean evidenceVerified;

    @NotNull
    private LocalDate purchasingDate;

    private LocalDate plantingDate;
    private String typeOfSugarcane;
    private String nomineeName;
    private String directorName;
}

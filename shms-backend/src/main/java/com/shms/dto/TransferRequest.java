package com.shms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransferRequest {

    @NotBlank
    private String fromMemberId;

    /** Optional at request time - the transferee may not be registered as a shareholder yet. */
    private String toMemberId;

    private String nomineeName;

    @NotBlank
    private String reasonType; // DEATH, SALE, GIFT

    private String reasonDetail;

    @NotNull
    private LocalDate transferDate;
}

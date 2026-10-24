package com.shms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransferDecisionRequest {

    /** APPROVED or REJECTED */
    @NotBlank
    private String decision;

    private String remarks;
}

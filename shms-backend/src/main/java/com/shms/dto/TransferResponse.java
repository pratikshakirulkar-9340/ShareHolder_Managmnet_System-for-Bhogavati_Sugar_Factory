package com.shms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {
    private Long id;
    private String fromMemberId;
    private String fromMemberName;
    private String toMemberId;
    private String nomineeName;
    private String reasonType;
    private String reasonDetail;
    private LocalDate transferDate;
    private String status;
    private Instant reviewedAt;
}

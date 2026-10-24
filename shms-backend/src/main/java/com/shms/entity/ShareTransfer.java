package com.shms.entity;

import com.shms.entity.enums.TransferReason;
import com.shms.entity.enums.TransferStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "share_transfers")
@Getter
@Setter
public class ShareTransfer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_shareholder_id", nullable = false)
    private Shareholder fromShareholder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_shareholder_id")
    private Shareholder toShareholder;

    @Column(name = "nominee_name", length = 150)
    private String nomineeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_type", nullable = false, length = 20)
    private TransferReason reasonType;

    @Column(name = "reason_detail", length = 255)
    private String reasonDetail;

    @Column(name = "transfer_date", nullable = false)
    private LocalDate transferDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransferStatus status = TransferStatus.PENDING;

    @Column(name = "requested_by")
    private Long requestedBy;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;
}

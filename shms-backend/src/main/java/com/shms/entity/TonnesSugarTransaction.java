package com.shms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tonnes_sugar_transactions")
@Getter
@Setter
public class TonnesSugarTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shareholder_id", nullable = false)
    private Shareholder shareholder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rate_id", nullable = false)
    private Rate rate;

    @Column(name = "total_tonnes", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalTonnes;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal entitlement;

    @Column(name = "supplied_sugar", nullable = false, precision = 10, scale = 2)
    private BigDecimal suppliedSugar;

    @Column(name = "previous_remaining", nullable = false, precision = 10, scale = 2)
    private BigDecimal previousRemaining = BigDecimal.ZERO;

    @Column(name = "remaining_sugar", nullable = false, precision = 10, scale = 2)
    private BigDecimal remainingSugar;

    @Column(name = "rate_per_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal ratePerKg;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "supplied_date", nullable = false)
    private LocalDate suppliedDate;
}

package com.shms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rates")
@Getter
@Setter
public class Rate extends BaseEntity {

    @Column(name = "sugar_factory_name", nullable = false, length = 150)
    private String sugarFactoryName;

    @Column(name = "share_purchase_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal sharePurchaseAmount;

    @Column(name = "per_month_share_sugar", nullable = false, precision = 10, scale = 2)
    private BigDecimal perMonthShareSugar;

    @Column(name = "rate_of_share_sugar", nullable = false, precision = 10, scale = 2)
    private BigDecimal rateOfShareSugar;

    @Column(name = "rate_of_tonnes_sugar", nullable = false, precision = 10, scale = 2)
    private BigDecimal rateOfTonnesSugar;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    /** NULL means this is the currently active rate. */
    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "created_by")
    private Long createdBy;
}

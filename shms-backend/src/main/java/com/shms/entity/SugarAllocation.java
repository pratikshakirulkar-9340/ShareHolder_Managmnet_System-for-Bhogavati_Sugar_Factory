package com.shms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "sugar_allocations")
@Getter
@Setter
public class SugarAllocation extends BaseEntity {

    @Column(name = "bonus_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal bonusAmount;

    @Column(name = "allocation_year", nullable = false)
    private Integer allocationYear;

    @Column(name = "applied_by")
    private Long appliedBy;

    @Column(name = "affected_shareholder_count", nullable = false)
    private Integer affectedShareholderCount;
}

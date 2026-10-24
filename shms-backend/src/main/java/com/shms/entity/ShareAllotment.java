package com.shms.entity;

import com.shms.entity.enums.ShareClass;
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

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "share_allotments")
@Getter
@Setter
public class ShareAllotment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shareholder_id", nullable = false)
    private Shareholder shareholder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rate_id", nullable = false)
    private Rate rate;

    @Column(name = "share_purchase_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal sharePurchaseAmount;

    @Column(name = "purchasing_date", nullable = false)
    private LocalDate purchasingDate;

    @Column(name = "per_month_sugar", nullable = false, precision = 10, scale = 2)
    private BigDecimal perMonthSugar;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_share", nullable = false, length = 20)
    private ShareClass typeOfShare;

    @Column(name = "planting_date")
    private LocalDate plantingDate;

    @Column(name = "type_of_sugarcane", length = 50)
    private String typeOfSugarcane;

    @Column(name = "nominee_name", length = 150)
    private String nomineeName;

    @Column(name = "director_name", length = 150)
    private String directorName;
}

package com.shms.entity;

import com.shms.entity.enums.ShareClass;
import com.shms.entity.enums.ShareholderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "shareholders")
@Getter
@Setter
public class Shareholder extends BaseEntity {

    @Column(name = "member_id", nullable = false, unique = true, length = 20)
    private String memberId;

    @Column(name = "member_name", nullable = false, length = 150)
    private String memberName;

    @Column(length = 100)
    private String taluka;

    @Column(length = 100)
    private String district;

    @Column(length = 100)
    private String village;

    private Integer gatNo;

    private Integer age;

    @Column(name = "farmer_land", length = 50)
    private String farmerLand;

    @Column(name = "contact_no", length = 20)
    private String contactNo;

    @Column(length = 10)
    private String gender;

    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    @Column(name = "nominee_name", length = 150)
    private String nomineeName;

    @Column(name = "director_name", length = 150)
    private String directorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "share_class", nullable = false, length = 20)
    private ShareClass shareClass = ShareClass.UNASSIGNED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShareholderStatus status = ShareholderStatus.ACTIVE;
}

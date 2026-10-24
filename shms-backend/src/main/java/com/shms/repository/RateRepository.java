package com.shms.repository;

import com.shms.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface RateRepository extends JpaRepository<Rate, Long> {

    Optional<Rate> findByEffectiveToIsNull();

    /** Resolves the rate that was in effect on a given date - used to keep historical transactions consistent. */
    @org.springframework.data.jpa.repository.Query(
            "SELECT r FROM Rate r WHERE r.effectiveFrom <= :date " +
            "AND (r.effectiveTo IS NULL OR r.effectiveTo >= :date) ORDER BY r.effectiveFrom DESC")
    Optional<Rate> findEffectiveOn(@org.springframework.data.repository.query.Param("date") LocalDate date);
}

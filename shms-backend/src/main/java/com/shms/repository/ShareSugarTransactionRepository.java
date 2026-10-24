package com.shms.repository;

import com.shms.entity.ShareSugarTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShareSugarTransactionRepository extends JpaRepository<ShareSugarTransaction, Long> {
    List<ShareSugarTransaction> findByShareholderIdOrderBySuppliedDateDesc(Long shareholderId);
    Optional<ShareSugarTransaction> findFirstByShareholderIdOrderByCreatedAtDesc(Long shareholderId);
    List<ShareSugarTransaction> findBySuppliedDateBetween(LocalDate from, LocalDate to);
}

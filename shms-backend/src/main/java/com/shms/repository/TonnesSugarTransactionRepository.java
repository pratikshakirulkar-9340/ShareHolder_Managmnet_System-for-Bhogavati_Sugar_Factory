package com.shms.repository;

import com.shms.entity.TonnesSugarTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TonnesSugarTransactionRepository extends JpaRepository<TonnesSugarTransaction, Long> {
    List<TonnesSugarTransaction> findByShareholderIdOrderBySuppliedDateDesc(Long shareholderId);
    Optional<TonnesSugarTransaction> findFirstByShareholderIdOrderByCreatedAtDesc(Long shareholderId);
    List<TonnesSugarTransaction> findBySuppliedDateBetween(LocalDate from, LocalDate to);
}

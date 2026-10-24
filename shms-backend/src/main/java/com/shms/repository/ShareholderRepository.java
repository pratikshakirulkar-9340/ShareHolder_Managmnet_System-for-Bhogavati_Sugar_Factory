package com.shms.repository;

import com.shms.entity.Shareholder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ShareholderRepository extends JpaRepository<Shareholder, Long>,
        JpaSpecificationExecutor<Shareholder> {
    Optional<Shareholder> findByMemberId(String memberId);
    boolean existsByMemberId(String memberId);
    long countByShareClass(com.shms.entity.enums.ShareClass shareClass);
}

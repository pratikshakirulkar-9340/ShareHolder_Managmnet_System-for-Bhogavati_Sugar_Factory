package com.shms.repository;

import com.shms.entity.ShareAllotment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShareAllotmentRepository extends JpaRepository<ShareAllotment, Long> {
    List<ShareAllotment> findByShareholderId(Long shareholderId);
    Optional<ShareAllotment> findFirstByShareholderIdOrderByCreatedAtDesc(Long shareholderId);
}

package com.shms.repository;

import com.shms.entity.ShareTransfer;
import com.shms.entity.enums.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ShareTransferRepository extends JpaRepository<ShareTransfer, Long> {
    List<ShareTransfer> findByStatus(TransferStatus status);
    List<ShareTransfer> findByTransferDateBetween(LocalDate from, LocalDate to);
    long countByStatus(TransferStatus status);
}

package com.backend.stockmaster.transfer.repository;

import com.backend.stockmaster.transfer.domain.Transfer;
import com.backend.stockmaster.transfer.domain.TransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    Page<Transfer> findByStatut(TransferStatus statut, Pageable pageable);
    List<Transfer> findByEntrepotSourceIdOrEntrepotDestinationId(Long src, Long dst);
    long count();
}

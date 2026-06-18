package com.backend.stockmaster.receipt.repository;

import com.backend.stockmaster.receipt.domain.GoodsReceipt;
import com.backend.stockmaster.receipt.domain.ReceiptStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoodsReceiptRepository extends JpaRepository<GoodsReceipt, Long> {
    Page<GoodsReceipt> findByEntrepotId(Long entrepotId, Pageable pageable);
    Page<GoodsReceipt> findByStatut(ReceiptStatus statut, Pageable pageable);
    Optional<GoodsReceipt> findByNumero(String numero);
    long count();
}

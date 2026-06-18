package com.backend.stockmaster.purchaseorder.repository;

import com.backend.stockmaster.purchaseorder.domain.POStatus;
import com.backend.stockmaster.purchaseorder.domain.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Page<PurchaseOrder> findByFournisseurId(Long fournisseurId, Pageable pageable);
    Page<PurchaseOrder> findByStatut(POStatus statut, Pageable pageable);
    long count();
}

package com.backend.stockmaster.inventory.repository;

import com.backend.stockmaster.inventory.domain.Inventory;
import com.backend.stockmaster.inventory.domain.InventoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Page<Inventory> findByStatut(InventoryStatus statut, Pageable pageable);
    Page<Inventory> findByEntrepotId(Long entrepotId, Pageable pageable);
    long count();
}

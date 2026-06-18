package com.backend.stockmaster.stock.repository;

import com.backend.stockmaster.stock.domain.MovementType;
import com.backend.stockmaster.stock.domain.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    Page<StockMovement> findByProduitId(Long produitId, Pageable pageable);
    Page<StockMovement> findByEntrepotSourceIdOrEntrepotDestinationId(Long src, Long dst, Pageable pageable);
    List<StockMovement> findByReferenceDocument(String ref);
    Page<StockMovement> findByType(MovementType type, Pageable pageable);
}

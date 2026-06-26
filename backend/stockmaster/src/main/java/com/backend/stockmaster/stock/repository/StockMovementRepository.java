package com.backend.stockmaster.stock.repository;

import com.backend.stockmaster.stock.domain.MovementType;
import com.backend.stockmaster.stock.domain.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    Page<StockMovement> findByProduitId(Long produitId, Pageable pageable);
    Page<StockMovement> findByEntrepotSourceIdOrEntrepotDestinationId(Long src, Long dst, Pageable pageable);
    List<StockMovement> findByReferenceDocument(String ref);
    Page<StockMovement> findByType(MovementType type, Pageable pageable);

    @Query("SELECT m FROM StockMovement m WHERE m.createdAt BETWEEN :debut AND :fin")
    Page<StockMovement> findByPeriode(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin,
            Pageable pageable);
}

package com.backend.stockmaster.stock.repository;

import com.backend.stockmaster.stock.domain.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProduitIdAndEntrepotId(Long produitId, Long entrepotId);

    /**
     * Recherche un stock avec verrou pessimiste en écriture.
     * Utilisé pour les opérations nécessitant une garantie de cohérence (ajustements, transferts).
     * Le verrou est maintenu jusqu'à la fin de la transaction.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.produitId = :produitId AND s.entrepotId = :entrepotId")
    Optional<Stock> findByProduitIdAndEntrepotIdForUpdate(@Param("produitId") Long produitId, @Param("entrepotId") Long entrepotId);

    List<Stock> findByEntrepotId(Long entrepotId);

    List<Stock> findByProduitId(Long produitId);

    List<Stock> findByQuantiteDisponibleLessThanAndProduitIdIn(Integer seuil, List<Long> produitIds);

    @Query("SELECT COUNT(s) FROM Stock s WHERE s.quantiteDisponible > 0")
    long countProductsInStock();

    @Query("""
        SELECT COUNT(s) FROM Stock s
        JOIN Product p ON s.produitId = p.id
        WHERE p.stockMinimum IS NOT NULL
          AND s.quantiteDisponible <= p.stockMinimum
    """)
    long countCriticalStockProducts();

    @Query("""
        SELECT COALESCE(SUM(s.quantiteDisponible * p.prixAchat), 0)
        FROM Stock s
        JOIN Product p ON s.produitId = p.id
        WHERE p.prixAchat IS NOT NULL
    """)
    java.math.BigDecimal calculateTotalStockValue();

    /**
     * Recherche les stocks critiques avec leurs produits associés en une seule requête.
     * Utilisé pour la génération d'alertes sans N+1 queries.
     */
    @Query("""
        SELECT s, p FROM Stock s
        JOIN Product p ON s.produitId = p.id
        WHERE (p.stockMinimum IS NOT NULL AND s.quantiteDisponible <= p.stockMinimum)
           OR (p.stockMaximum IS NOT NULL AND s.quantiteDisponible >= p.stockMaximum)
    """)
    List<Object[]> findCriticalStocksWithProduct();
}

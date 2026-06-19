package com.backend.stockmaster.stock.repository;

import com.backend.stockmaster.stock.domain.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProduitIdAndEntrepotId(Long produitId, Long entrepotId);

    List<Stock> findByEntrepotId(Long entrepotId);

    List<Stock> findByProduitId(Long produitId);

    List<Stock> findByQuantiteDisponibleLessThanAndProduitIdIn(Integer seuil, List<Long> produitIds);
}

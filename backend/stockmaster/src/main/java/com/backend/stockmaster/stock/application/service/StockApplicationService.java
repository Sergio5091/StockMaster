package com.backend.stockmaster.stock.application.service;

import com.backend.stockmaster.stock.application.dto.*;
import com.backend.stockmaster.stock.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockApplicationService {

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;

    public void addStock(Long produitId, Long entrepotId, Integer quantite, com.backend.stockmaster.stock.domain.MovementType type, String reference, Object document, String username) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }

    public void removeStock(Long produitId, Long entrepotId, Integer quantite, com.backend.stockmaster.stock.domain.MovementType type, String reference, Object document, String username) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }

    public StockDTO findByProduitAndEntrepot(Long produitId, Long entrepotId) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }

    public Page<StockMovementDTO> findMovementsByProduit(Long produitId, Pageable pageable) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }

    public void updateStock(Long produitId, Long entrepotId, int quantite, String referenceDocument, String username) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }

    public void decreaseStock(Long produitId, Long entrepotId, int quantite, String referenceDocument, String username) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }

    public Page<StockDTO> findAll(Pageable pageable) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }

    public List<StockDTO> findByWarehouse(Long warehouseId) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }

    public Page<StockMovementDTO> findMovements(Pageable pageable) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }
}
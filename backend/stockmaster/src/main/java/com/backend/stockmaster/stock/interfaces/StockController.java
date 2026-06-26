package com.backend.stockmaster.stock.interfaces;

import com.backend.stockmaster.stock.application.dto.StockDTO;
import com.backend.stockmaster.stock.application.dto.StockMovementDTO;
import com.backend.stockmaster.stock.application.service.StockApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Tag(name = "Stocks", description = "Consultation des niveaux de stock")
public class StockController {

    private final StockApplicationService stockService;

    @GetMapping
    @Operation(summary = "Lister tous les stocks (paginé)")
    public ResponseEntity<Page<StockDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(stockService.findAll(pageable));
    }

    @GetMapping("/{produitId}/{entrepotId}")
    @Operation(summary = "Stock d'un produit dans un entrepôt")
    public ResponseEntity<StockDTO> findByProduitAndEntrepot(
            @PathVariable Long produitId, @PathVariable Long entrepotId) {
        return ResponseEntity.ok(stockService.findByProduitAndEntrepot(produitId, entrepotId));
    }

    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "Tous les stocks d'un entrepôt")
    public ResponseEntity<List<StockDTO>> findByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(stockService.findByWarehouse(warehouseId));
    }

    // Conservé pour rétrocompatibilité — préférer /api/stock-movements
    @GetMapping("/movements")
    @Operation(summary = "Historique de tous les mouvements (déprécié, utiliser /api/stock-movements)")
    public ResponseEntity<Page<StockMovementDTO>> findMovements(Pageable pageable) {
        return ResponseEntity.ok(stockService.findMovements(pageable));
    }

    // Conservé pour rétrocompatibilité
    @GetMapping("/movements/product/{produitId}")
    @Operation(summary = "Mouvements d'un produit (déprécié, utiliser /api/stock-movements/product/{produitId})")
    public ResponseEntity<Page<StockMovementDTO>> findMovementsByProduit(
            @PathVariable Long produitId, Pageable pageable) {
        return ResponseEntity.ok(stockService.findMovementsByProduit(produitId, pageable));
    }
}

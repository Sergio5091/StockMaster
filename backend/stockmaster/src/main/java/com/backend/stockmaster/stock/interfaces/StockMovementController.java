package com.backend.stockmaster.stock.interfaces;

import com.backend.stockmaster.stock.application.dto.StockMovementDTO;
import com.backend.stockmaster.stock.application.dto.StockUpdateRequest;
import com.backend.stockmaster.stock.application.service.StockApplicationService;
import com.backend.stockmaster.stock.domain.MovementType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
@Tag(name = "Mouvements de stock", description = "Historique et ajustements manuels de stock")
public class StockMovementController {

    private final StockApplicationService stockService;

    @GetMapping
    @Operation(summary = "Lister tous les mouvements avec filtres optionnels")
    public ResponseEntity<Page<StockMovementDTO>> findAll(
            @Parameter(description = "Filtrer par entrepôt (source ou destination)")
            @RequestParam(required = false) Long entrepotId,
            @Parameter(description = "Filtrer par type de mouvement")
            @RequestParam(required = false) MovementType type,
            @Parameter(description = "Date de début (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            Pageable pageable) {

        if (entrepotId != null) {
            return ResponseEntity.ok(stockService.findMovementsByEntrepot(entrepotId, pageable));
        }
        if (type != null) {
            return ResponseEntity.ok(stockService.findMovementsByType(type, pageable));
        }
        if (dateDebut != null && dateFin != null) {
            return ResponseEntity.ok(stockService.findMovementsByPeriode(dateDebut, dateFin, pageable));
        }
        return ResponseEntity.ok(stockService.findMovements(pageable));
    }

    @GetMapping("/product/{produitId}")
    @Operation(summary = "Mouvements d'un produit spécifique")
    public ResponseEntity<Page<StockMovementDTO>> findByProduit(
            @PathVariable Long produitId, Pageable pageable) {
        return ResponseEntity.ok(stockService.findMovementsByProduit(produitId, pageable));
    }

    @PostMapping("/adjust")
    @Operation(summary = "Ajustement manuel de stock (entrée ou sortie avec justification)")
    public ResponseEntity<StockMovementDTO> adjust(@Valid @RequestBody StockUpdateRequest request) {
        StockMovementDTO result = stockService.ajusterStock(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}

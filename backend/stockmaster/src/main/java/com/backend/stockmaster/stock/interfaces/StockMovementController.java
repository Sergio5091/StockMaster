package com.backend.stockmaster.stock.interfaces;

import com.backend.stockmaster.stock.application.dto.StockMovementDTO;
import com.backend.stockmaster.stock.application.service.StockApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
@Tag(name = "Mouvements de stock", description = "Historique et ajustements manuels")
public class StockMovementController {

    private final StockApplicationService stockService;

    @GetMapping
    @Operation(summary = "Historique de tous les mouvements (paginé)")
    public ResponseEntity<Page<StockMovementDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(stockService.findMovements(pageable));
    }

    @GetMapping("/product/{produitId}")
    @Operation(summary = "Mouvements d'un produit")
    public ResponseEntity<Page<StockMovementDTO>> findByProduit(
            @PathVariable Long produitId, Pageable pageable) {
        return ResponseEntity.ok(stockService.findMovementsByProduit(produitId, pageable));
    }

    /**
     * Ajustement manuel : permet d'ajouter ou retirer des unités avec une justification.
     * Quantité positive = entrée, quantité négative = sortie.
     */
    @PostMapping("/adjust")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE')")
    @Operation(summary = "Ajustement manuel du stock (entrée ou sortie avec justification)")
    public ResponseEntity<StockMovementDTO> adjust(@Valid @RequestBody AdjustRequest req) {
        String user = currentUser();
        String note = req.getJustification() + " [par " + user + "]";

        // Utilise ajusterStock qui retourne le mouvement créé
        com.backend.stockmaster.stock.application.dto.StockUpdateRequest updateReq =
                new com.backend.stockmaster.stock.application.dto.StockUpdateRequest();
        updateReq.setProduitId(req.getProduitId());
        updateReq.setEntrepotId(req.getEntrepotId());
        updateReq.setQuantite(req.getQuantite()); // positif ou négatif
        updateReq.setJustification(note);

        StockMovementDTO movement = stockService.ajusterStock(updateReq);
        return ResponseEntity.ok(movement);
    }

    private String currentUser() {
        try { return SecurityContextHolder.getContext().getAuthentication().getName(); }
        catch (Exception e) { return "system"; }
    }

    // DTO interne pour l'ajustement
    @Data
    public static class AdjustRequest {
        @NotNull private Long produitId;
        @NotNull private Long entrepotId;
        @NotNull private Integer quantite; // positif = entrée, négatif = sortie
        @NotBlank private String justification;
    }
}

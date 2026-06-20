package com.backend.stockmaster.stock.application.dto;

import com.backend.stockmaster.stock.domain.MovementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateRequest {

    @NotNull(message = "Le produit est obligatoire")
    private Long produitId;

    @NotNull(message = "L'entrepôt est obligatoire")
    private Long entrepotId;

    /**
     * Quantité à ajouter (positive) ou retirer (négative).
     * Doit être différente de zéro.
     */
    @NotNull(message = "La quantité est obligatoire")
    private Integer quantite;

    /**
     * Type de mouvement explicite. Si null, déterminé automatiquement :
     * quantite > 0 → ENTREE, quantite < 0 → SORTIE
     */
    private MovementType type;

    @NotBlank(message = "La justification est obligatoire pour la traçabilité")
    private String justification;
}

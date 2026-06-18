package com.backend.stockmaster.stock.application.dto;

import com.backend.stockmaster.stock.domain.MovementType;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementDTO {
    private Long id;
    private MovementType type;
    private Long produitId;
    private String produitRef;
    private String produitNom;
    private Long entrepotSourceId;
    private String entrepotSourceNom;
    private Long entrepotDestinationId;
    private String entrepotDestinationNom;
    private Integer quantite;
    private Integer quantiteAvant;
    private Integer quantiteApres;
    private String referenceDocument;
    private String utilisateurNom;
    private String note;
    private LocalDateTime createdAt;
}

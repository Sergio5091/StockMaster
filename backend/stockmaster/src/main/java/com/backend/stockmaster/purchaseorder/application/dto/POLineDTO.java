package com.backend.stockmaster.purchaseorder.application.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class POLineDTO {
    private Long id;
    private Long produitId;
    private String produitNom;
    private Integer quantiteCommandee;
    private Integer quantiteRecue;
    private BigDecimal prixUnitaire;
    private BigDecimal montantLigne;
}

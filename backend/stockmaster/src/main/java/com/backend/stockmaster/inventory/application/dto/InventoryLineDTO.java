package com.backend.stockmaster.inventory.application.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InventoryLineDTO {
    private Long id;
    private Long produitId;
    private String produitNom;
    private String produitRef;
    private Integer quantiteTheorique;
    private Integer quantiteComptee;
    private Integer ecart;
    private boolean ajuste;
    private String note;
}

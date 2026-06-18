package com.backend.stockmaster.receipt.application.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReceiptLineDTO {
    private Long id;
    private Long produitId;
    private String produitNom;
    private Integer quantiteAttendue;
    private Integer quantiteRecue;
    private boolean qualiteOk;
    private String noteQualite;
}

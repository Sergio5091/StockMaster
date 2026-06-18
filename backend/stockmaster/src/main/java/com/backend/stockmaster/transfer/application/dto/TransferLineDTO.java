package com.backend.stockmaster.transfer.application.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TransferLineDTO {
    private Long id;
    private Long produitId;
    private String produitNom;
    private Integer quantiteDemandee;
    private Integer quantiteRecue;
}

package com.backend.stockmaster.stock.application.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    private Long id;
    private Long produitId;
    private String produitRef;
    private String produitNom;
    private Long entrepotId;
    private String entrepotCode;
    private String entrepotNom;
    private Integer quantiteDisponible;
    private Integer quantiteReservee;
    private Integer quantiteEnTransit;
    private Integer stockMinimum;
    private Integer stockMaximum;
    private String statut; // critical, low, normal, excess
}

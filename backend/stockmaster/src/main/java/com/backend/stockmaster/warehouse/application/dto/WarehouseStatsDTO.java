package com.backend.stockmaster.warehouse.application.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseStatsDTO {

    private Long warehouseId;
    private String nom;
    private Double capaciteTotale;
    private Double capaciteUtilisee;
    private Double tauxOccupation;
    private Long nombreZones;
    private Long nombreProduits;
}

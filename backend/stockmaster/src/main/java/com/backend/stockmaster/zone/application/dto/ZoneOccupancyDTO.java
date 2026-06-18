package com.backend.stockmaster.zone.application.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneOccupancyDTO {
    private Long zoneId;
    private String nom;
    private Double capaciteM3;
    private Double occupationM3;
    private Double tauxOccupation;
    private long nombreEmplacements;
    private long emplacementsLibres;
}

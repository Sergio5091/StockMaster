package com.backend.stockmaster.location.application.dto;

import com.backend.stockmaster.location.domain.LocationStatus;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LocationDTO {
    private Long id;
    private String code;
    private Long zoneId;
    private String zoneNom;
    private String rayon;
    private String etagere;
    private String position;
    private Double capaciteKg;
    private Double poidsActuel;
    private LocationStatus statut;
    private Long produitId;
    private String produitNom;
}

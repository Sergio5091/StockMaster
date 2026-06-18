package com.backend.stockmaster.zone.application.dto;

import com.backend.stockmaster.zone.domain.ZoneType;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneDTO {
    private Long id;
    private String code;
    private String nom;
    private ZoneType type;
    private Long entrepotId;
    private String entrepotNom;
    private Double capaciteM3;
    private Double occupationM3;
    private Double tauxOccupation;
    private boolean actif;
    private LocalDateTime createdAt;
}

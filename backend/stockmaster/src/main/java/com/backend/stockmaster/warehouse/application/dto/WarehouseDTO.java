package com.backend.stockmaster.warehouse.application.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDTO {

    private Long id;
    private String code;
    private String nom;
    private String adresse;
    private String ville;
    private String pays;
    private Long responsableId;
    private String responsableUsername;
    private Double capaciteTotale;
    private boolean actif;
    private String telephone;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

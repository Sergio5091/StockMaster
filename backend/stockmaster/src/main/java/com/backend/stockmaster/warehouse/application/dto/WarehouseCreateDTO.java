package com.backend.stockmaster.warehouse.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseCreateDTO {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    private String adresse;

    @NotBlank(message = "La ville est obligatoire")
    private String ville;

    private String pays;
    private Long responsableId;
    private Double capaciteTotale;
    private String telephone;
    private String email;
}

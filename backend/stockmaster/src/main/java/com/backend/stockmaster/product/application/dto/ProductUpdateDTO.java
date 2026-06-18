package com.backend.stockmaster.product.application.dto;

import com.backend.stockmaster.product.domain.UniteMesure;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDTO {

    @NotBlank(message = "Le nom du produit est obligatoire")
    private String nom;

    @NotNull(message = "La catégorie est obligatoire")
    private Long categorieId;

    private String description;
    private Long fournisseurPrincipalId;
    private String codeBarre;
    private BigDecimal prixAchat;
    private BigDecimal prixVente;
    private Double poidsKg;
    private Double volumeM3;
    private Integer stockMinimum;
    private Integer stockMaximum;
    private UniteMesure uniteMesure;
}

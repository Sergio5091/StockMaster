package com.backend.stockmaster.product.application.dto;

import com.backend.stockmaster.product.domain.UniteMesure;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String reference;
    private String codeBarre;
    private String nom;
    private String description;
    private Long categorieId;
    private String categorieNom;
    private Long fournisseurPrincipalId;
    private String fournisseurNom;
    private BigDecimal prixAchat;
    private BigDecimal prixVente;
    private Double poidsKg;
    private Double volumeM3;
    private Integer stockMinimum;
    private Integer stockMaximum;
    private UniteMesure uniteMesure;
    private boolean actif;
    private String imageUrl;
    private String niveauStock; // CRITIQUE, FAIBLE, NORMAL, EXCESSIF
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

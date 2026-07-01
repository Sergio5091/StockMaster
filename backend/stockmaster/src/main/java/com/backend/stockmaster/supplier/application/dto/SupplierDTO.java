package com.backend.stockmaster.supplier.application.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDTO {

    private Long id;
    private String code;
    private String nom;
    private String adresse;
    private String ville;
    private String pays;
    private String telephone;
    private String email;
    private String contactPrincipalNom;
    private String contactPrincipalPrenom;
    private String contactPrincipalTelephone;
    private String contactPrincipalEmail;
    private Integer delaiLivraisonJours;
    private boolean actif;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Stats calculées (nombre de commandes, ponctualité, montant total)
    @Builder.Default
    private Long commandesTotal = 0L;

    @Builder.Default
    private Double tauxRespectDelai = 0.0;

    @Builder.Default
    private Double montantTotal = 0.0;
}

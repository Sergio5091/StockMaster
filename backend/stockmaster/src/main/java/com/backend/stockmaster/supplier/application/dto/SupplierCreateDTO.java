package com.backend.stockmaster.supplier.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierCreateDTO {

    @NotBlank(message = "Le nom du fournisseur est obligatoire")
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
}

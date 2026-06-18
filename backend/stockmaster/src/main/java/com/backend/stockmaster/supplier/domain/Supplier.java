package com.backend.stockmaster.supplier.domain;

import com.backend.stockmaster.core.audit.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "suppliers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Supplier extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @NotBlank
    @Column(nullable = false)
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

    @Builder.Default
    @Column(nullable = false)
    private boolean actif = true;
}

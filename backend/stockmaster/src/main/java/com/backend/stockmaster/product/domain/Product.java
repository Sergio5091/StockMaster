package com.backend.stockmaster.product.domain;

import com.backend.stockmaster.core.audit.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Product extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(unique = true)
    private String codeBarre;

    @NotBlank
    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "categorie_id")
    private Long categorieId;

    @Column(name = "fournisseur_principal_id")
    private Long fournisseurPrincipalId;

    @Column(precision = 10, scale = 2)
    private BigDecimal prixAchat;

    @Column(precision = 10, scale = 2)
    private BigDecimal prixVente;

    private Double poidsKg;
    private Double volumeM3;

    @Builder.Default
    private Integer stockMinimum = 0;

    @Builder.Default
    private Integer stockMaximum = 9999;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UniteMesure uniteMesure = UniteMesure.UNITE;

    @Builder.Default
    @Column(nullable = false)
    private boolean actif = true;

    private String imageUrl;
}

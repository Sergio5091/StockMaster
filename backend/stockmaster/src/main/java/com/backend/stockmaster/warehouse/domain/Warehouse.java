package com.backend.stockmaster.warehouse.domain;

import com.backend.stockmaster.core.audit.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "warehouses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Warehouse extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @NotBlank
    @Column(nullable = false)
    private String nom;

    private String adresse;

    @NotBlank
    @Column(nullable = false)
    private String ville;

    private String pays;

    @Column(name = "responsable_id")
    private Long responsableId;

    private Double capaciteTotale;

    @Builder.Default
    @Column(nullable = false)
    private boolean actif = true;

    private String telephone;
    private String email;
}

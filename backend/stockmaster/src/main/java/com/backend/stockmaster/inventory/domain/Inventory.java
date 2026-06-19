package com.backend.stockmaster.inventory.domain;

import com.backend.stockmaster.core.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "inventories")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Inventory extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String numero;
    @Column(name = "entrepot_id", nullable = false) private Long entrepotId;
    @Column(name = "zone_id") private Long zoneId;
    @Column(name = "categorie_id") private Long categorieId;
    @Enumerated(EnumType.STRING) @Builder.Default private InventoryType type = InventoryType.COMPLET;
    @Enumerated(EnumType.STRING) @Builder.Default private InventoryStatus statut = InventoryStatus.PLANIFIE;
    private LocalDate datePlanifiee;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    @Column(columnDefinition = "TEXT") private String note;
    private String creePar;
    private String valideePar;
    @Builder.Default
    @OneToMany(mappedBy = "inventaire", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryLine> lignes = new ArrayList<>();
}

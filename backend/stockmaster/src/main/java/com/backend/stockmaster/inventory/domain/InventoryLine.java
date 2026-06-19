package com.backend.stockmaster.inventory.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "inventory_lines")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InventoryLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventaire_id", nullable = false)
    private Inventory inventaire;
    @Column(name = "produit_id", nullable = false) private Long produitId;
    @Builder.Default private Integer quantiteTheorique = 0;
    @Builder.Default private Integer quantiteComptee = 0;
    @Builder.Default private Integer ecart = 0;
    private boolean ajuste;
    private String note;
}

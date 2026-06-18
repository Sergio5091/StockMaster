package com.backend.stockmaster.purchaseorder.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "purchase_order_lines")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PurchaseOrderLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    private PurchaseOrder commande;
    @Column(name = "produit_id", nullable = false) private Long produitId;
    @Builder.Default private Integer quantiteCommandee = 0;
    @Builder.Default private Integer quantiteRecue = 0;
    @Builder.Default private BigDecimal prixUnitaire = BigDecimal.ZERO;
    @Builder.Default private BigDecimal montantLigne = BigDecimal.ZERO;
}

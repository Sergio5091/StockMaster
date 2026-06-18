package com.backend.stockmaster.purchaseorder.domain;

import com.backend.stockmaster.core.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "purchase_orders")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PurchaseOrder extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String numero;
    @Column(name = "fournisseur_id", nullable = false) private Long fournisseurId;
    @Column(name = "entrepot_destination_id", nullable = false) private Long entrepotDestinationId;
    @Enumerated(EnumType.STRING) @Builder.Default private POStatus statut = POStatus.BROUILLON;
    private LocalDate dateCommande;
    private LocalDate dateLivraisonPrevue;
    private LocalDate dateLivraisonReelle;
    @Builder.Default private BigDecimal montantTotal = BigDecimal.ZERO;
    @Column(columnDefinition = "TEXT") private String note;
    private String creePar;
    private String valideeePar;
    @Builder.Default
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderLine> lignes = new ArrayList<>();
}

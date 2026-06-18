package com.backend.stockmaster.receipt.domain;

import com.backend.stockmaster.core.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "goods_receipts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GoodsReceipt extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(name = "fournisseur_id", nullable = false)
    private Long fournisseurId;

    @Column(name = "entrepot_id", nullable = false)
    private Long entrepotId;

    @Column(name = "commande_fournisseur_id")
    private Long commandeFournisseurId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReceiptStatus statut = ReceiptStatus.BROUILLON;

    private LocalDate dateReception;

    @Column(columnDefinition = "TEXT")
    private String note;

    private String creePar;
    private String valideePar;
    private LocalDate dateValidation;

    @Builder.Default
    @OneToMany(mappedBy = "bonReception", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GoodsReceiptLine> lignes = new ArrayList<>();
}

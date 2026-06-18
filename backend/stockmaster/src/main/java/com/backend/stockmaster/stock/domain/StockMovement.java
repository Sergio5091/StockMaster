package com.backend.stockmaster.stock.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    @Column(name = "produit_id", nullable = false)
    private Long produitId;

    @Column(name = "entrepot_source_id")
    private Long entrepotSourceId;

    @Column(name = "entrepot_destination_id")
    private Long entrepotDestinationId;

    @Column(nullable = false)
    private Integer quantite;

    @Column(nullable = false)
    private Integer quantiteAvant;

    @Column(nullable = false)
    private Integer quantiteApres;

    private String referenceDocument;

    @Column(name = "utilisateur_id")
    private Long utilisateurId;

    @Column(columnDefinition = "TEXT")
    private String note;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}

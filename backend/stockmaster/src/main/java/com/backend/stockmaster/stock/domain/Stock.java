package com.backend.stockmaster.stock.domain;

import com.backend.stockmaster.core.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stocks",
       uniqueConstraints = @UniqueConstraint(columnNames = {"produit_id", "entrepot_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Stock extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "produit_id", nullable = false)
    private Long produitId;

    @Column(name = "entrepot_id", nullable = false)
    private Long entrepotId;

    @Builder.Default
    @Column(nullable = false)
    private Integer quantiteDisponible = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer quantiteReservee = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer quantiteEnTransit = 0;
}

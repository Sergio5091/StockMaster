package com.backend.stockmaster.location.domain;

import com.backend.stockmaster.core.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "locations")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Location extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String code;
    @Column(name = "zone_id", nullable = false) private Long zoneId;
    private String rayon;
    private String etagere;
    private String position;
    @Builder.Default private Double capaciteKg = 0.0;
    @Builder.Default private Double poidsActuel = 0.0;
    @Enumerated(EnumType.STRING) @Builder.Default private LocationStatus statut = LocationStatus.LIBRE;
    @Column(name = "produit_id") private Long produitId;
    @Builder.Default private boolean actif = true;
}

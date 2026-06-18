package com.backend.stockmaster.zone.domain;

import com.backend.stockmaster.core.audit.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "zones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Zone extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @NotBlank
    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ZoneType type;

    @Column(name = "entrepot_id", nullable = false)
    private Long entrepotId;

    @Builder.Default
    private Double capaciteM3 = 0.0;

    @Builder.Default
    private Double occupationM3 = 0.0;

    @Builder.Default
    @Column(nullable = false)
    private boolean actif = true;
}

package com.backend.stockmaster.transfer.domain;

import com.backend.stockmaster.core.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "transfers")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Transfer extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String numero;
    @Column(name = "entrepot_source_id", nullable = false) private Long entrepotSourceId;
    @Column(name = "entrepot_destination_id", nullable = false) private Long entrepotDestinationId;
    @Enumerated(EnumType.STRING) @Builder.Default private TransferStatus statut = TransferStatus.BROUILLON;
    private LocalDate dateExpedition;
    private LocalDate dateReception;
    private String creePar;
    private String expedieePar;
    private String recuePar;
    @Column(columnDefinition = "TEXT") private String note;
    @Builder.Default
    @OneToMany(mappedBy = "transfer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferLine> lignes = new ArrayList<>();
}

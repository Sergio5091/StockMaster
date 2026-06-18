package com.backend.stockmaster.transfer.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "transfer_lines")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TransferLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_id", nullable = false)
    private Transfer transfer;
    @Column(name = "produit_id", nullable = false) private Long produitId;
    @Builder.Default private Integer quantiteDemandee = 0;
    private Integer quantiteRecue;
}

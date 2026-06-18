package com.backend.stockmaster.issue.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "goods_issue_lines")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GoodsIssueLine {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bon_sortie_id", nullable = false)
    private GoodsIssue bonSortie;

    @Column(name = "produit_id", nullable = false)
    private Long produitId;

    @Builder.Default private Integer quantiteDemandee = 0;
    @Builder.Default private Integer quantiteSortie = 0;
}

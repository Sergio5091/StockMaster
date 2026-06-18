package com.backend.stockmaster.receipt.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "goods_receipt_lines")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsReceiptLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bon_reception_id", nullable = false)
    private GoodsReceipt bonReception;

    @Column(name = "produit_id", nullable = false)
    private Long produitId;

    @Builder.Default
    private Integer quantiteAttendue = 0;

    @Builder.Default
    private Integer quantiteRecue = 0;

    @Builder.Default
    private boolean qualiteOk = true;

    private String noteQualite;
}

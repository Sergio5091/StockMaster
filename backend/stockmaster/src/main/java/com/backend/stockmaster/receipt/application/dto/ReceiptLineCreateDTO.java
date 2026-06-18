package com.backend.stockmaster.receipt.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReceiptLineCreateDTO {
    @NotNull private Long produitId;
    private Integer quantiteAttendue;
    @NotNull private Integer quantiteRecue;
    private boolean qualiteOk = true;
    private String noteQualite;
}

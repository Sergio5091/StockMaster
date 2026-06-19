package com.backend.stockmaster.purchaseorder.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class POLineCreateDTO {
    @NotNull private Long produitId;
    @NotNull @Min(1) private Integer quantiteCommandee;
    @Builder.Default private BigDecimal prixUnitaire = BigDecimal.ZERO;
}

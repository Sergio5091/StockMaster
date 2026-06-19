package com.backend.stockmaster.inventory.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InventoryCountDTO {
    @NotNull private Long produitId;
    @NotNull @Min(0) private Integer quantiteComptee;
    private String note;
}

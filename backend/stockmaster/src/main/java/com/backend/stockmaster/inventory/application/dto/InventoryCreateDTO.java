package com.backend.stockmaster.inventory.application.dto;

import com.backend.stockmaster.inventory.domain.InventoryType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InventoryCreateDTO {
    @NotNull private Long entrepotId;
    private Long zoneId;
    private Long categorieId;
    @Builder.Default private InventoryType type = InventoryType.COMPLET;
    @NotNull private LocalDate datePlanifiee;
    private String note;
}

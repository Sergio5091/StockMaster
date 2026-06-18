package com.backend.stockmaster.transfer.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TransferLineCreateDTO {
    @NotNull private Long produitId;
    @NotNull @Min(1) private Integer quantiteDemandee;
}

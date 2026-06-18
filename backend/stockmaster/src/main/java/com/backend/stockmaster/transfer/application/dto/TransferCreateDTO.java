package com.backend.stockmaster.transfer.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TransferCreateDTO {
    @NotNull private Long entrepotSourceId;
    @NotNull private Long entrepotDestinationId;
    private String note;
    @Builder.Default
    private List<TransferLineCreateDTO> lignes = new ArrayList<>();
}

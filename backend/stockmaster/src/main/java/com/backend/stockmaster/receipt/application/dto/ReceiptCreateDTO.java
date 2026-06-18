package com.backend.stockmaster.receipt.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReceiptCreateDTO {
    @NotNull private Long fournisseurId;
    @NotNull private Long entrepotId;
    private Long commandeFournisseurId;
    @NotNull private LocalDate dateReception;
    private String note;
    @Builder.Default
    private List<ReceiptLineCreateDTO> lignes = new ArrayList<>();
}

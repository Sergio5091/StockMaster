package com.backend.stockmaster.purchaseorder.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class POCreateDTO {
    @NotNull private Long fournisseurId;
    @NotNull private Long entrepotDestinationId;
    private LocalDate dateCommande;
    private LocalDate dateLivraisonPrevue;
    private String note;
    @Builder.Default
    private List<POLineCreateDTO> lignes = new ArrayList<>();
}

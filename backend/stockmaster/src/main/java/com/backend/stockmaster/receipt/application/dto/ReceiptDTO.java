package com.backend.stockmaster.receipt.application.dto;

import com.backend.stockmaster.receipt.domain.ReceiptStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReceiptDTO {
    private Long id;
    private String numero;
    private Long fournisseurId;
    private String fournisseurNom;
    private Long entrepotId;
    private String entrepotNom;
    private Long commandeFournisseurId;
    private ReceiptStatus statut;
    private LocalDate dateReception;
    private String note;
    private String creePar;
    private String valideePar;
    private LocalDate dateValidation;
    private List<ReceiptLineDTO> lignes;
}

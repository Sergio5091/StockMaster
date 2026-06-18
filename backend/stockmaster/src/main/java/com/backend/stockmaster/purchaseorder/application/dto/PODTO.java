package com.backend.stockmaster.purchaseorder.application.dto;

import com.backend.stockmaster.purchaseorder.domain.POStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PODTO {
    private Long id;
    private String numero;
    private Long fournisseurId;
    private String fournisseurNom;
    private Long entrepotDestinationId;
    private String entrepotNom;
    private POStatus statut;
    private LocalDate dateCommande;
    private LocalDate dateLivraisonPrevue;
    private LocalDate dateLivraisonReelle;
    private BigDecimal montantTotal;
    private String note;
    private String creePar;
    private String valideeePar;
    private List<POLineDTO> lignes;
}

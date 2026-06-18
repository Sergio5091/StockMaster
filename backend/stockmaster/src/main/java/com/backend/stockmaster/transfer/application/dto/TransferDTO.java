package com.backend.stockmaster.transfer.application.dto;

import com.backend.stockmaster.transfer.domain.TransferStatus;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TransferDTO {
    private Long id;
    private String numero;
    private Long entrepotSourceId;
    private String entrepotSourceNom;
    private Long entrepotDestinationId;
    private String entrepotDestinationNom;
    private TransferStatus statut;
    private LocalDate dateExpedition;
    private LocalDate dateReception;
    private String creePar;
    private String expedieePar;
    private String recuePar;
    private String note;
    private List<TransferLineDTO> lignes;
}

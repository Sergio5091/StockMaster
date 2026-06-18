package com.backend.stockmaster.issue.application.dto;

import com.backend.stockmaster.issue.domain.IssueMotif;
import com.backend.stockmaster.issue.domain.IssueStatus;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class IssueDTO {
    private Long id;
    private String numero;
    private Long entrepotId;
    private String entrepotNom;
    private IssueMotif motif;
    private String clientNom;
    private String clientReference;
    private IssueStatus statut;
    private LocalDate dateSortie;
    private String note;
    private String creePar;
    private String valideePar;
    private List<IssueLineDTO> lignes;
}

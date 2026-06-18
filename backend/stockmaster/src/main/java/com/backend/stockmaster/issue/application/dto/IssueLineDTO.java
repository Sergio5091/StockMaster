package com.backend.stockmaster.issue.application.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class IssueLineDTO {
    private Long id;
    private Long produitId;
    private String produitNom;
    private Integer quantiteDemandee;
    private Integer quantiteSortie;
}

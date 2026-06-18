package com.backend.stockmaster.issue.application.dto;

import com.backend.stockmaster.issue.domain.IssueMotif;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class IssueCreateDTO {
    @NotNull private Long entrepotId;
    @NotNull private IssueMotif motif;
    private String clientNom;
    private String clientReference;
    @NotNull private LocalDate dateSortie;
    private String note;
    @Builder.Default
    private List<IssueLineCreateDTO> lignes = new ArrayList<>();
}

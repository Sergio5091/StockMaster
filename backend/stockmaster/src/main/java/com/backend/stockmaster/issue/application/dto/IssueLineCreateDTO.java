package com.backend.stockmaster.issue.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class IssueLineCreateDTO {
    @NotNull private Long produitId;
    @NotNull @Min(1) private Integer quantiteDemandee;
}

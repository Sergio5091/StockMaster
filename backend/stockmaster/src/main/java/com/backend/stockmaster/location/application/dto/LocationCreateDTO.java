package com.backend.stockmaster.location.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LocationCreateDTO {
    private String rayon;
    private String etagere;
    private String position;
    @Builder.Default private Double capaciteKg = 0.0;
}

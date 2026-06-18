package com.backend.stockmaster.zone.application.dto;

import com.backend.stockmaster.zone.domain.ZoneType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneCreateDTO {
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotNull(message = "Le type est obligatoire")
    private ZoneType type;

    private Double capaciteM3;
}

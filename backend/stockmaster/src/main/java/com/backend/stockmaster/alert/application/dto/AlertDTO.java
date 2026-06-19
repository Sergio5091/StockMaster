package com.backend.stockmaster.alert.application.dto;

import com.backend.stockmaster.alert.domain.AlertType;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AlertDTO {
    private Long id;
    private AlertType type;
    private String titre;
    private String message;
    private Long entrepotId;
    private String entrepotNom;
    private boolean traitee;
    private String traiteeParUsername;
    private LocalDateTime traiteeAt;
    private LocalDateTime createdAt;
}

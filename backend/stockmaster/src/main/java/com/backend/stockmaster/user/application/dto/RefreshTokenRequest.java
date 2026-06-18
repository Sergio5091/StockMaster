package com.backend.stockmaster.user.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotBlank(message = "Le refresh token est requis")
    private String refreshToken;
}

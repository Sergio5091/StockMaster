package com.backend.stockmaster.user.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequest {

    @NotBlank
    private String newPassword;
}

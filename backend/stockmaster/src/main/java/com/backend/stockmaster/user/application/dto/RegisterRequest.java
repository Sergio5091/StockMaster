package com.backend.stockmaster.user.application.dto;

import com.backend.stockmaster.core.security.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Email
    private String email;

    private String fullName;

    @NotNull
    private Role role;
}

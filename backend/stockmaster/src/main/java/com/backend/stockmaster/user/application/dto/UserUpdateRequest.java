package com.backend.stockmaster.user.application.dto;

import com.backend.stockmaster.core.security.Role;
import lombok.Data;

@Data
public class UserUpdateRequest {

    private String username;
    private String email;
    private String fullName;
    private Role role;
    private Boolean active;
}

package com.backend.stockmaster.user.interfaces;

import com.backend.stockmaster.user.application.dto.PasswordResetRequest;
import com.backend.stockmaster.user.application.dto.UserResponse;
import com.backend.stockmaster.user.application.dto.UserUpdateRequest;
import com.backend.stockmaster.user.application.service.UserApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRATEUR')")
public class UserController {

    private final UserApplicationService userApplicationService;

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(userApplicationService.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userApplicationService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userApplicationService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id,
            @Valid @RequestBody PasswordResetRequest request) {
        userApplicationService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }
}

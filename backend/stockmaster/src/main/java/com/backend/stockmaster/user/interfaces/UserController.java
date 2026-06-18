package com.backend.stockmaster.user.interfaces;

import com.backend.stockmaster.user.application.dto.PasswordResetRequest;
import com.backend.stockmaster.user.application.dto.RegisterRequest;
import com.backend.stockmaster.user.application.dto.UserResponse;
import com.backend.stockmaster.user.application.dto.UserUpdateRequest;
import com.backend.stockmaster.user.application.service.UserApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Gestion des utilisateurs")
public class UserController {

    private final UserApplicationService userApplicationService;

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Rechercher des utilisateurs")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean actif,
            Pageable pageable) {
        return ResponseEntity.ok(userApplicationService.findAllWithFilters(role, actif, pageable));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Lister tous les utilisateurs (paginé)")
    public ResponseEntity<Page<UserResponse>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(userApplicationService.findAll(pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Créer un utilisateur")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userApplicationService.createUser(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Obtenir un utilisateur par ID")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userApplicationService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Modifier un utilisateur")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userApplicationService.updateUser(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Désactiver un utilisateur")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userApplicationService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Réactiver un utilisateur")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userApplicationService.activateUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Réinitialiser le mot de passe d'un utilisateur")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id,
            @Valid @RequestBody PasswordResetRequest request) {
        userApplicationService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }
}

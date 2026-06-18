package com.backend.stockmaster.user.interfaces;

import com.backend.stockmaster.core.config.JwtConfig;
import com.backend.stockmaster.core.security.JwtTokenProvider;
import com.backend.stockmaster.user.application.dto.AuthResponse;
import com.backend.stockmaster.user.application.dto.LoginRequest;
import com.backend.stockmaster.user.application.dto.RefreshTokenRequest;
import com.backend.stockmaster.user.application.service.RefreshTokenService;
import com.backend.stockmaster.user.domain.RefreshToken;
import com.backend.stockmaster.user.domain.User;
import com.backend.stockmaster.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Login, refresh token, logout")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final JwtConfig jwtConfig;

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String accessToken = jwtTokenProvider.generateToken(userDetails.getUsername(), roles);

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .build();

        return ResponseEntity.ok(AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getExpirationMs())
                .user(userInfo)
                .build());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renouveler le token d'accès")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());
        User user = refreshToken.getUser();

        List<String> roles = List.of("ROLE_" + user.getRole().name());
        String newAccessToken = jwtTokenProvider.generateToken(user.getUsername(), roles);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getExpirationMs())
                .build());
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnexion — révoque le refresh token")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            userRepository.findByUsername(userDetails.getUsername())
                    .ifPresent(refreshTokenService::revokeAllByUser);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Profil de l'utilisateur connecté")
    public ResponseEntity<AuthResponse.UserInfo> me(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();
        return ResponseEntity.ok(AuthResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .build());
    }
}

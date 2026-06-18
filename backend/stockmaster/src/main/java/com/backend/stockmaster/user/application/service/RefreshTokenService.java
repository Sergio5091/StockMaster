package com.backend.stockmaster.user.application.service;

import com.backend.stockmaster.core.exception.BusinessException;
import com.backend.stockmaster.user.domain.RefreshToken;
import com.backend.stockmaster.user.domain.User;
import com.backend.stockmaster.user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    // 7 jours par défaut
    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Révoquer les anciens tokens de cet utilisateur
        refreshTokenRepository.revokeAllByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Refresh token invalide"));

        if (refreshToken.isRevoked()) {
            throw new BusinessException("Refresh token révoqué");
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("Refresh token expiré");
        }

        return refreshToken;
    }

    @Transactional
    public void revokeAllByUser(User user) {
        refreshTokenRepository.revokeAllByUser(user);
    }
}

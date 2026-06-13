package com.backend.stockmaster.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.jwt")
@Data
public class JwtConfig {
    private String secret;
    private long expirationMs;
}

package com.backend.stockmaster.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
public class AuditAndScheduleConfig {
}

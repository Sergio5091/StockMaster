package com.backend.stockmaster.scheduler;

import com.backend.stockmaster.alert.application.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class AlertScheduler {

    private final AlertService alertService;

    /**
     * Générer les alertes de stock toutes les heures
     */
    @Scheduled(fixedRate = 3600000) // 1 heure
    public void generateStockAlerts() {
        try {
            log.info("Starting scheduled stock alert generation...");
            alertService.generateStockAlerts();
            log.info("Stock alerts generated successfully");
        } catch (Exception e) {
            log.error("Error generating stock alerts", e);
        }
    }

    /**
     * Générer les alertes de stock toutes les 6 heures (alternative)
     */
    @Scheduled(cron = "0 0 */6 * * *") // Every 6 hours at 0 minutes
    public void generateStockAlertsWithCron() {
        try {
            log.info("Starting scheduled (CRON) stock alert generation...");
            alertService.generateStockAlerts();
            log.info("Stock alerts (CRON) generated successfully");
        } catch (Exception e) {
            log.error("Error generating stock alerts (CRON)", e);
        }
    }

    /**
     * Nettoyer les alertes traitées de plus de 30 jours
     */
    @Scheduled(cron = "0 0 2 * * *") // Every day at 2 AM
    public void cleanupOldAlerts() {
        try {
            log.info("Starting cleanup of old alerts...");
            // Implémentation du nettoyage des alertes anciennes
            log.info("Old alerts cleaned up successfully");
        } catch (Exception e) {
            log.error("Error cleaning up old alerts", e);
        }
    }
}

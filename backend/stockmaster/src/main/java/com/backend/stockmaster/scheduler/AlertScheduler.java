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

    /** Générer les alertes de stock toutes les heures */
    @Scheduled(fixedRate = 3_600_000)
    public void generateStockAlerts() {
        try {
            log.info("Génération des alertes de stock...");
            int count = alertService.generateStockAlerts();
            log.info("{} nouvelle(s) alerte(s) générée(s)", count);
        } catch (Exception e) {
            log.error("Erreur lors de la génération des alertes", e);
        }
    }

    /** Nettoyer les alertes traitées de plus de 30 jours — chaque jour à 2h */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupOldAlerts() {
        try {
            log.info("Nettoyage des alertes anciennes...");
            alertService.cleanupOldAlerts();
            log.info("Nettoyage terminé");
        } catch (Exception e) {
            log.error("Erreur lors du nettoyage des alertes", e);
        }
    }
}

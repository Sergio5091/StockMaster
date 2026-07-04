package com.backend.stockmaster.alert.interfaces;

import com.backend.stockmaster.alert.application.dto.AlertDTO;
import com.backend.stockmaster.alert.application.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Tag(name = "Alertes", description = "Gestion des alertes de stock")
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE', 'MAGASINIER', 'AUDITEUR')")
    @Operation(summary = "Lister toutes les alertes (paginé)")
    public ResponseEntity<Page<AlertDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(alertService.findAll(pageable));
    }

    @GetMapping("/unread")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE', 'MAGASINIER', 'AUDITEUR')")
    @Operation(summary = "Alertes non traitées")
    public ResponseEntity<Page<AlertDTO>> findUnread(Pageable pageable) {
        return ResponseEntity.ok(alertService.findUnread(pageable));
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE', 'MAGASINIER', 'AUDITEUR')")
    @Operation(summary = "10 dernières alertes non traitées")
    public ResponseEntity<List<AlertDTO>> findRecent() {
        return ResponseEntity.ok(alertService.findRecent());
    }

    @GetMapping("/count-unread")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE', 'MAGASINIER', 'AUDITEUR')")
    @Operation(summary = "Nombre d'alertes non traitées")
    public ResponseEntity<Map<String, Long>> countUnread() {
        return ResponseEntity.ok(Map.of("count", alertService.countUnread()));
    }

    @PostMapping("/{id}/treat")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE')")
    @Operation(summary = "Marquer une alerte comme traitée")
    public ResponseEntity<AlertDTO> treat(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.markTreated(id));
    }

    @PostMapping("/{id}/reopen")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE')")
    @Operation(summary = "Rouvrir une alerte")
    public ResponseEntity<AlertDTO> reopen(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.reopen(id));
    }

    @PostMapping("/treat-all")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE')")
    @Operation(summary = "Marquer toutes les alertes comme traitées")
    public ResponseEntity<Void> treatAll() {
        alertService.markAllTreated();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE')")
    @Operation(summary = "Générer les alertes depuis l'état du stock courant")
    public ResponseEntity<Map<String, Integer>> generate() {
        int n = alertService.generateStockAlerts();
        return ResponseEntity.ok(Map.of("generated", n));
    }
}

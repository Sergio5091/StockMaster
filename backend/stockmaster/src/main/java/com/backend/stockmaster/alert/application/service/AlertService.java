package com.backend.stockmaster.alert.application.service;

import com.backend.stockmaster.alert.application.dto.AlertDTO;
import com.backend.stockmaster.alert.domain.Alert;
import com.backend.stockmaster.alert.domain.AlertType;
import com.backend.stockmaster.alert.repository.AlertRepository;
import com.backend.stockmaster.core.exception.ResourceNotFoundException;
import com.backend.stockmaster.stock.repository.StockRepository;
import com.backend.stockmaster.product.repository.ProductRepository;
import com.backend.stockmaster.warehouse.repository.WarehouseRepository;
import com.backend.stockmaster.zone.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertService {

    private final AlertRepository alertRepository;
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final ZoneRepository zoneRepository;

    public Page<AlertDTO> findAll(Pageable pageable) {
        return alertRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<AlertDTO> findUnread(Pageable pageable) {
        return alertRepository.findByTraitee(false, pageable).map(this::toDTO);
    }

    public long countUnread() {
        return alertRepository.countByTraiteeFalse();
    }

    public List<AlertDTO> findRecent() {
        return alertRepository.findTop10ByTraiteeFalseOrderByCreatedAtDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public AlertDTO markTreated(Long id) {
        Alert alert = findOrThrow(id);
        alert.setTraitee(true);
        alert.setTraiteeParUsername(currentUser());
        alert.setTraiteeAt(LocalDateTime.now());
        return toDTO(alertRepository.save(alert));
    }

    @Transactional
    public void markAllTreated() {
        alertRepository.findByTraitee(false, Pageable.unpaged()).forEach(a -> {
            a.setTraitee(true);
            a.setTraiteeAt(LocalDateTime.now());
            a.setTraiteeParUsername(currentUser());
        });
    }

    @Transactional
    public AlertDTO reopen(Long id) {
        Alert alert = findOrThrow(id);
        alert.setTraitee(false);
        alert.setTraiteeAt(null);
        alert.setTraiteeParUsername(null);
        return toDTO(alertRepository.save(alert));
    }

    /** Analyse les stocks et génère les alertes critiques/excessives manquantes */
    @Transactional
    public int generateStockAlerts() {
        // Charger en une seule requête les titres des alertes non traitées existantes
        // pour éviter le O(n²) avec findAll() dans chaque itération
        java.util.Set<String> existingTitles = alertRepository.findByTraitee(false, Pageable.unpaged())
                .stream()
                .map(Alert::getTitre)
                .collect(java.util.stream.Collectors.toSet());

        int[] generated = {0};

        for (var stock : stockRepository.findAll()) {
            productRepository.findById(stock.getProduitId()).ifPresent(product -> {
                // Alerte stock critique (<=  minimum)
                if (product.getStockMinimum() != null && stock.getQuantiteDisponible() <= product.getStockMinimum()) {
                    String titre = "Stock critique : " + product.getNom();
                    if (!existingTitles.contains(titre)) {
                        alertRepository.save(Alert.builder()
                                .type(AlertType.STOCK_CRITIQUE)
                                .titre(titre)
                                .message("Quantité disponible : " + stock.getQuantiteDisponible()
                                        + " (seuil minimum : " + product.getStockMinimum() + ")")
                                .entrepotId(stock.getEntrepotId())
                                .build());
                        existingTitles.add(titre);
                        generated[0]++;
                    }
                }
                // Alerte stock excédentaire (>= maximum)
                if (product.getStockMaximum() != null && stock.getQuantiteDisponible() >= product.getStockMaximum()) {
                    String titre = "Stock excédentaire : " + product.getNom();
                    if (!existingTitles.contains(titre)) {
                        alertRepository.save(Alert.builder()
                                .type(AlertType.STOCK_EXCESSIF)
                                .titre(titre)
                                .message("Quantité : " + stock.getQuantiteDisponible()
                                        + " (seuil maximum : " + product.getStockMaximum() + ")")
                                .entrepotId(stock.getEntrepotId())
                                .build());
                        existingTitles.add(titre);
                        generated[0]++;
                    }
                }
            });
        }

        // Zones saturées (> 90%)
        zoneRepository.findAll().stream()
                .filter(z -> z.getCapaciteM3() != null && z.getCapaciteM3() > 0
                        && z.getOccupationM3() != null
                        && (z.getOccupationM3() / z.getCapaciteM3()) >= 0.90)
                .forEach(z -> {
                    String titre = "Zone saturée : " + z.getNom();
                    if (!existingTitles.contains(titre)) {
                        alertRepository.save(Alert.builder()
                                .type(AlertType.ZONE_SATUREE)
                                .titre(titre)
                                .message("Taux d'occupation : "
                                        + Math.round(z.getOccupationM3() / z.getCapaciteM3() * 100) + "%")
                                .entrepotId(z.getEntrepotId())
                                .build());
                        existingTitles.add(titre);
                        generated[0]++;
                    }
                });

        return generated[0];
    }

    /** Supprime les alertes traitées de plus de 30 jours */
    @Transactional
    public void cleanupOldAlerts() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        alertRepository.findByTraitee(true, Pageable.unpaged()).forEach(a -> {
            if (a.getTraiteeAt() != null && a.getTraiteeAt().isBefore(cutoff)) {
                alertRepository.delete(a);
            }
        });
    }

    private Alert findOrThrow(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerte introuvable : " + id));
    }

    private String currentUser() {
        try { return SecurityContextHolder.getContext().getAuthentication().getName(); }
        catch (Exception e) { return "system"; }
    }

    private AlertDTO toDTO(Alert a) {
        AlertDTO dto = AlertDTO.builder()
                .id(a.getId()).type(a.getType()).titre(a.getTitre())
                .message(a.getMessage()).entrepotId(a.getEntrepotId())
                .entrepotNom(a.getEntrepotNom()).traitee(a.isTraitee())
                .traiteeParUsername(a.getTraiteeParUsername())
                .traiteeAt(a.getTraiteeAt()).createdAt(a.getCreatedAt()).build();
        if (a.getEntrepotId() != null && (a.getEntrepotNom() == null))
            warehouseRepository.findById(a.getEntrepotId())
                    .ifPresent(w -> dto.setEntrepotNom(w.getNom()));
        return dto;
    }
}

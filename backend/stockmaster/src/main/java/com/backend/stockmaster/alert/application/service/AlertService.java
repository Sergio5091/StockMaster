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
        int generated = 0;
        for (var stock : stockRepository.findAll()) {
            productRepository.findById(stock.getProduitId()).ifPresent(product -> {
                if (product.getStockMinimum() != null && stock.getQuantiteDisponible() <= product.getStockMinimum()) {
                    String titre = "Stock critique : " + product.getNom();
                    boolean exists = alertRepository.findAll().stream()
                            .anyMatch(a -> !a.isTraitee() && a.getTitre().equals(titre));
                    if (!exists) {
                        alertRepository.save(Alert.builder()
                                .type(AlertType.STOCK_CRITIQUE)
                                .titre(titre)
                                .message("Quantité disponible : " + stock.getQuantiteDisponible() + " (seuil : " + product.getStockMinimum() + ")")
                                .entrepotId(stock.getEntrepotId())
                                .build());
                        // generated counter via outer scope not possible with lambda — use field trick
                    }
                }
                if (product.getStockMaximum() != null && stock.getQuantiteDisponible() >= product.getStockMaximum()) {
                    String titre = "Stock excédentaire : " + product.getNom();
                    boolean exists = alertRepository.findAll().stream()
                            .anyMatch(a -> !a.isTraitee() && a.getTitre().equals(titre));
                    if (!exists) {
                        alertRepository.save(Alert.builder()
                                .type(AlertType.STOCK_EXCESSIF).titre(titre)
                                .message("Quantité : " + stock.getQuantiteDisponible() + " (max : " + product.getStockMaximum() + ")")
                                .entrepotId(stock.getEntrepotId()).build());
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
                    boolean exists = alertRepository.findAll().stream()
                            .anyMatch(a -> !a.isTraitee() && a.getTitre().equals(titre));
                    if (!exists)
                        alertRepository.save(Alert.builder()
                                .type(AlertType.ZONE_SATUREE).titre(titre)
                                .message("Taux d'occupation : " + Math.round(z.getOccupationM3() / z.getCapaciteM3() * 100) + "%")
                                .entrepotId(z.getEntrepotId()).build());
                });
        return generated;
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

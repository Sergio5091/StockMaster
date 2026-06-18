package com.backend.stockmaster.zone.interfaces;

import com.backend.stockmaster.zone.application.dto.ZoneCreateDTO;
import com.backend.stockmaster.zone.application.dto.ZoneDTO;
import com.backend.stockmaster.zone.application.dto.ZoneOccupancyDTO;
import com.backend.stockmaster.zone.application.service.ZoneApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Zones", description = "Gestion des zones d'entrepôt")
public class ZoneController {

    private final ZoneApplicationService zoneService;

    @GetMapping("/api/warehouses/{warehouseId}/zones")
    @Operation(summary = "Lister les zones d'un entrepôt")
    public ResponseEntity<List<ZoneDTO>> findByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(zoneService.findByWarehouse(warehouseId));
    }

    @PostMapping("/api/warehouses/{warehouseId}/zones")
    @PreAuthorize("hasRole('ADMINISTRATEUR') or hasRole('GESTIONNAIRE')")
    @Operation(summary = "Créer une zone dans un entrepôt")
    public ResponseEntity<ZoneDTO> create(@PathVariable Long warehouseId,
            @Valid @RequestBody ZoneCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(zoneService.createZone(warehouseId, dto));
    }

    @GetMapping("/api/zones/{id}")
    @Operation(summary = "Obtenir une zone par ID")
    public ResponseEntity<ZoneDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(zoneService.findById(id));
    }

    @DeleteMapping("/api/zones/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Désactiver une zone (soft delete)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        zoneService.deleteZone(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/zones/{id}/occupancy")
    @Operation(summary = "Taux d'occupation d'une zone")
    public ResponseEntity<ZoneOccupancyDTO> getOccupancy(@PathVariable Long id) {
        return ResponseEntity.ok(zoneService.getOccupancy(id));
    }
}

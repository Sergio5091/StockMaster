package com.backend.stockmaster.location.interfaces;

import com.backend.stockmaster.location.application.dto.*;
import com.backend.stockmaster.location.application.service.LocationService;
import com.backend.stockmaster.location.domain.LocationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Emplacements", description = "Gestion des emplacements physiques")
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/api/zones/{zoneId}/locations")
    @Operation(summary = "Lister les emplacements d'une zone")
    public ResponseEntity<List<LocationDTO>> findByZone(@PathVariable Long zoneId) {
        return ResponseEntity.ok(locationService.findByZone(zoneId));
    }

    @PostMapping("/api/zones/{zoneId}/locations")
    @Operation(summary = "Créer un emplacement dans une zone")
    public ResponseEntity<LocationDTO> create(@PathVariable Long zoneId,
            @Valid @RequestBody LocationCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(locationService.create(zoneId, dto));
    }

    @GetMapping("/api/locations/{id}")
    @Operation(summary = "Obtenir un emplacement")
    public ResponseEntity<LocationDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.findById(id));
    }

    @PatchMapping("/api/locations/{id}/status")
    @Operation(summary = "Changer le statut d'un emplacement")
    public ResponseEntity<LocationDTO> updateStatus(@PathVariable Long id,
            @RequestParam LocationStatus statut) {
        return ResponseEntity.ok(locationService.updateStatus(id, statut));
    }

    @DeleteMapping("/api/locations/{id}")
    @Operation(summary = "Désactiver un emplacement")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

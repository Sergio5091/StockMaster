package com.backend.stockmaster.inventory.interfaces;

import com.backend.stockmaster.inventory.application.dto.*;
import com.backend.stockmaster.inventory.application.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
@Tag(name = "Inventaires", description = "Gestion des inventaires physiques")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE', 'MAGASINIER', 'AUDITEUR')")
    @Operation(summary = "Lister les inventaires")
    public ResponseEntity<Page<InventoryDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(inventoryService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE', 'MAGASINIER', 'AUDITEUR')")
    @Operation(summary = "Obtenir un inventaire")
    public ResponseEntity<InventoryDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE')")
    @Operation(summary = "Planifier un inventaire")
    public ResponseEntity<InventoryDTO> create(@Valid @RequestBody InventoryCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.create(dto));
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE')")
    @Operation(summary = "Démarrer l'inventaire (charge les lignes théoriques)")
    public ResponseEntity<InventoryDTO> start(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.start(id));
    }

    @PostMapping("/{id}/count")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE', 'MAGASINIER')")
    @Operation(summary = "Saisir le comptage d'un produit")
    public ResponseEntity<InventoryDTO> count(@PathVariable Long id, @Valid @RequestBody InventoryCountDTO dto) {
        return ResponseEntity.ok(inventoryService.count(id, dto));
    }

    @PostMapping("/{id}/validate")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE')")
    @Operation(summary = "Valider l'inventaire et ajuster le stock automatiquement")
    public ResponseEntity<InventoryDTO> validate(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.validate(id));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE')")
    @Operation(summary = "Annuler un inventaire")
    public ResponseEntity<InventoryDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.cancel(id));
    }
}

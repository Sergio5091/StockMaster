package com.backend.stockmaster.warehouse.interfaces;

import com.backend.stockmaster.warehouse.application.dto.*;
import com.backend.stockmaster.warehouse.application.service.WarehouseApplicationService;
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
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@Tag(name = "Entrepôts", description = "Gestion des entrepôts")
public class WarehouseController {

    private final WarehouseApplicationService warehouseService;

    @GetMapping
    @Operation(summary = "Lister tous les entrepôts")
    public ResponseEntity<List<WarehouseDTO>> findAll() {
        return ResponseEntity.ok(warehouseService.findAll());
    }

    @GetMapping("/active")
    @Operation(summary = "Lister les entrepôts actifs")
    public ResponseEntity<List<WarehouseDTO>> findActiveWarehouses() {
        return ResponseEntity.ok(warehouseService.findActiveWarehouses());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un entrepôt par ID")
    public ResponseEntity<WarehouseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE')")
    @Operation(summary = "Créer un entrepôt")
    public ResponseEntity<WarehouseDTO> create(@Valid @RequestBody WarehouseCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(warehouseService.createWarehouse(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE')")
    @Operation(summary = "Modifier un entrepôt")
    public ResponseEntity<WarehouseDTO> update(@PathVariable Long id,
            @Valid @RequestBody WarehouseUpdateDTO dto) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Désactiver un entrepôt")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}

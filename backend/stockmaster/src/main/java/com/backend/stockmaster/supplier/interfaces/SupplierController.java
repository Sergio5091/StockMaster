package com.backend.stockmaster.supplier.interfaces;

import com.backend.stockmaster.supplier.application.dto.SupplierCreateDTO;
import com.backend.stockmaster.supplier.application.dto.SupplierDTO;
import com.backend.stockmaster.supplier.application.dto.SupplierUpdateDTO;
import com.backend.stockmaster.supplier.application.service.SupplierService;
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
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Tag(name = "Fournisseurs", description = "Gestion des fournisseurs")
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    @Operation(summary = "Lister les fournisseurs (paginé)")
    public ResponseEntity<Page<SupplierDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(supplierService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un fournisseur par ID")
    public ResponseEntity<SupplierDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR') or hasRole('GESTIONNAIRE')")
    @Operation(summary = "Créer un fournisseur")
    public ResponseEntity<SupplierDTO> create(@Valid @RequestBody SupplierCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.createSupplier(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR') or hasRole('GESTIONNAIRE')")
    @Operation(summary = "Modifier un fournisseur")
    public ResponseEntity<SupplierDTO> update(@PathVariable Long id,
            @Valid @RequestBody SupplierUpdateDTO dto) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, dto));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Désactiver un fournisseur")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        supplierService.deactivateSupplier(id);
        return ResponseEntity.noContent().build();
    }
}

package com.backend.stockmaster.purchaseorder.interfaces;

import com.backend.stockmaster.purchaseorder.application.dto.*;
import com.backend.stockmaster.purchaseorder.application.service.PurchaseOrderService;
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
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Commandes fournisseurs", description = "Gestion des commandes d'achat")
public class PurchaseOrderController {

    private final PurchaseOrderService orderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE', 'AUDITEUR')")
    @Operation(summary = "Lister les commandes")
    public ResponseEntity<Page<PODTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(orderService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE', 'AUDITEUR')")
    @Operation(summary = "Obtenir une commande")
    public ResponseEntity<PODTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE')")
    @Operation(summary = "Créer une commande")
    public ResponseEntity<PODTO> create(@Valid @RequestBody POCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(dto));
    }

    @PostMapping("/{id}/validate")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE')")
    @Operation(summary = "Valider une commande brouillon")
    public ResponseEntity<PODTO> validate(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.validate(id));
    }

    @PostMapping("/{id}/send")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE')")
    @Operation(summary = "Envoyer une commande validée au fournisseur")
    public ResponseEntity<PODTO> send(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.send(id));
    }

    @PostMapping("/{id}/deliver")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE', 'MAGASINIER')")
    @Operation(summary = "Marquer comme livrée (réception partielle ou totale)")
    public ResponseEntity<PODTO> deliver(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.markDelivered(id));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE')")
    @Operation(summary = "Annuler une commande")
    public ResponseEntity<PODTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancel(id));
    }
}

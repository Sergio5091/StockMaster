package com.backend.stockmaster.transfer.interfaces;

import com.backend.stockmaster.transfer.application.dto.TransferCreateDTO;
import com.backend.stockmaster.transfer.application.dto.TransferDTO;
import com.backend.stockmaster.transfer.application.service.TransferService;
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
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Tag(name = "Transferts", description = "Transferts de stock entre entrepôts")
public class TransferController {

    private final TransferService transferService;

    @GetMapping
    @Operation(summary = "Lister tous les transferts")
    public ResponseEntity<Page<TransferDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(transferService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un transfert par ID")
    public ResponseEntity<TransferDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(transferService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE', 'MAGASINIER')")
    @Operation(summary = "Créer un transfert")
    public ResponseEntity<TransferDTO> create(@Valid @RequestBody TransferCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transferService.create(dto));
    }

    @PostMapping("/{id}/ship")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE')")
    @Operation(summary = "Expédier le transfert (déduit du stock source)")
    public ResponseEntity<TransferDTO> ship(@PathVariable Long id) {
        return ResponseEntity.ok(transferService.ship(id));
    }

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE', 'MAGASINIER')")
    @Operation(summary = "Réceptionner le transfert (ajoute au stock destination)")
    public ResponseEntity<TransferDTO> receive(@PathVariable Long id) {
        return ResponseEntity.ok(transferService.receive(id));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'GESTIONNAIRE')")
    @Operation(summary = "Annuler un transfert")
    public ResponseEntity<TransferDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(transferService.cancel(id));
    }
}

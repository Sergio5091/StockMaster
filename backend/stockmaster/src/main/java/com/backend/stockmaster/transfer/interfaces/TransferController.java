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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Tag(name = "Transferts", description = "Transferts de stock entre entrepôts")
public class TransferController {

    private final TransferService transferService;

    @GetMapping
    public ResponseEntity<Page<TransferDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(transferService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(transferService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TransferDTO> create(@Valid @RequestBody TransferCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transferService.create(dto));
    }

    @PostMapping("/{id}/ship")
    @Operation(summary = "Expédier le transfert (déduit du stock source)")
    public ResponseEntity<TransferDTO> ship(@PathVariable Long id) {
        return ResponseEntity.ok(transferService.ship(id));
    }

    @PostMapping("/{id}/receive")
    @Operation(summary = "Réceptionner le transfert (ajoute au stock destination)")
    public ResponseEntity<TransferDTO> receive(@PathVariable Long id) {
        return ResponseEntity.ok(transferService.receive(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<TransferDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(transferService.cancel(id));
    }
}

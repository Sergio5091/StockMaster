package com.backend.stockmaster.receipt.interfaces;

import com.backend.stockmaster.receipt.application.dto.ReceiptCreateDTO;
import com.backend.stockmaster.receipt.application.dto.ReceiptDTO;
import com.backend.stockmaster.receipt.application.service.GoodsReceiptService;
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
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
@Tag(name = "Bons de réception", description = "Gestion des entrées de stock")
public class GoodsReceiptController {

    private final GoodsReceiptService receiptService;

    @GetMapping
    @Operation(summary = "Lister les bons de réception")
    public ResponseEntity<Page<ReceiptDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(receiptService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un bon de réception")
    public ResponseEntity<ReceiptDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(receiptService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un bon de réception")
    public ResponseEntity<ReceiptDTO> create(@Valid @RequestBody ReceiptCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(receiptService.create(dto));
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "Soumettre pour validation")
    public ResponseEntity<ReceiptDTO> submit(@PathVariable Long id) {
        return ResponseEntity.ok(receiptService.submit(id));
    }

    @PostMapping("/{id}/validate")
    @Operation(summary = "Valider le bon (met à jour le stock)")
    public ResponseEntity<ReceiptDTO> validate(@PathVariable Long id) {
        return ResponseEntity.ok(receiptService.validate(id));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Rejeter le bon")
    public ResponseEntity<ReceiptDTO> reject(@PathVariable Long id) {
        return ResponseEntity.ok(receiptService.reject(id));
    }
}

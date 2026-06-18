package com.backend.stockmaster.issue.interfaces;

import com.backend.stockmaster.issue.application.dto.IssueCreateDTO;
import com.backend.stockmaster.issue.application.dto.IssueDTO;
import com.backend.stockmaster.issue.application.service.GoodsIssueService;
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
@RequestMapping("/api/issues")
@RequiredArgsConstructor
@Tag(name = "Bons de sortie", description = "Gestion des sorties de stock")
public class GoodsIssueController {

    private final GoodsIssueService issueService;

    @GetMapping
    @Operation(summary = "Lister les bons de sortie")
    public ResponseEntity<Page<IssueDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(issueService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un bon de sortie")
    public ResponseEntity<IssueDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un bon de sortie")
    public ResponseEntity<IssueDTO> create(@Valid @RequestBody IssueCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.create(dto));
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "Soumettre pour validation")
    public ResponseEntity<IssueDTO> submit(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.submit(id));
    }

    @PostMapping("/{id}/validate")
    @Operation(summary = "Valider et déduire le stock")
    public ResponseEntity<IssueDTO> validate(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.validate(id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Annuler un bon de sortie")
    public ResponseEntity<IssueDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.cancel(id));
    }
}

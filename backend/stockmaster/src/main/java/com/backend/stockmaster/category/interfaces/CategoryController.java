package com.backend.stockmaster.category.interfaces;

import com.backend.stockmaster.category.application.dto.CategoryCreateDTO;
import com.backend.stockmaster.category.application.dto.CategoryDTO;
import com.backend.stockmaster.category.application.dto.CategoryUpdateDTO;
import com.backend.stockmaster.category.application.service.CategoryApplicationService;
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
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Catégories", description = "Gestion des catégories de produits")
public class CategoryController {

    private final CategoryApplicationService categoryService;

    @GetMapping
    @Operation(summary = "Lister toutes les catégories actives")
    public ResponseEntity<List<CategoryDTO>> findAll() {
        return ResponseEntity.ok(categoryService.findActiveCategories());
    }

    @GetMapping("/active")
    @Operation(summary = "Lister les catégories actives")
    public ResponseEntity<List<CategoryDTO>> findActiveCategories() {
        return ResponseEntity.ok(categoryService.findActiveCategories());
    }

    @GetMapping("/roots")
    @Operation(summary = "Lister les catégories racines (sans parent)")
    public ResponseEntity<List<CategoryDTO>> findRoots() {
        return ResponseEntity.ok(categoryService.findRootCategories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une catégorie par ID")
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @GetMapping("/{id}/children")
    @Operation(summary = "Obtenir les sous-catégories")
    public ResponseEntity<List<CategoryDTO>> findChildren(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findSubCategories(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Créer une catégorie")
    public ResponseEntity<CategoryDTO> create(@Valid @RequestBody CategoryCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Modifier une catégorie")
    public ResponseEntity<CategoryDTO> update(@PathVariable Long id,
            @Valid @RequestBody CategoryUpdateDTO dto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Désactiver une catégorie (soft delete)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}

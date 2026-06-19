package com.backend.stockmaster.product.interfaces;

import com.backend.stockmaster.product.application.dto.ProductCreateDTO;
import com.backend.stockmaster.product.application.dto.ProductDTO;
import com.backend.stockmaster.product.application.dto.ProductUpdateDTO;
import com.backend.stockmaster.product.application.service.ProductService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Produits", description = "Gestion des produits")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Lister les produits (paginé, filtres optionnels)")
    public ResponseEntity<Page<ProductDTO>> findAll(
            @RequestParam(required = false) Long categorieId,
            @RequestParam(required = false) Long fournisseurId,
            Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable, categorieId, fournisseurId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un produit par ID")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des produits")
    public ResponseEntity<Page<ProductDTO>> search(@RequestParam String q, Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(q, pageable));
    }

    @GetMapping("/barcode/{code}")
    @Operation(summary = "Trouver un produit par code-barre")
    public ResponseEntity<ProductDTO> findByBarcode(@PathVariable String code) {
        return ResponseEntity.ok(productService.findByBarcode(code));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR') or hasRole('GESTIONNAIRE')")
    @Operation(summary = "Créer un produit")
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR') or hasRole('GESTIONNAIRE')")
    @Operation(summary = "Modifier un produit")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Désactiver un produit")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        productService.deactivateProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMINISTRATEUR') or hasRole('GESTIONNAIRE')")
    @Operation(summary = "Uploader une image pour un produit")
    public ResponseEntity<ProductDTO> uploadImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file) throws IOException {
        String uploadDir = "uploads/products/";
        Files.createDirectories(Paths.get(uploadDir));
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path dest = Paths.get(uploadDir + filename);
        Files.copy(file.getInputStream(), dest);
        String imageUrl = "/uploads/products/" + filename;
        return ResponseEntity.ok(productService.updateImageUrl(id, imageUrl));
    }
}

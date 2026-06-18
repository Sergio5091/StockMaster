package com.backend.stockmaster.product.application.service;

import com.backend.stockmaster.category.repository.CategoryRepository;
import com.backend.stockmaster.core.exception.BusinessException;
import com.backend.stockmaster.core.exception.ResourceNotFoundException;
import com.backend.stockmaster.product.application.dto.ProductCreateDTO;
import com.backend.stockmaster.product.application.dto.ProductDTO;
import com.backend.stockmaster.product.application.dto.ProductUpdateDTO;
import com.backend.stockmaster.product.application.mapper.ProductMapper;
import com.backend.stockmaster.product.domain.Product;
import com.backend.stockmaster.product.repository.ProductRepository;
import com.backend.stockmaster.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    @Transactional
    public ProductDTO createProduct(ProductCreateDTO dto) {
        categoryRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Catégorie introuvable avec l'id : " + dto.getCategorieId()));

        String reference = generateUniqueReference();
        Product product = productMapper.toEntity(dto);
        product.setReference(reference);
        product.setActif(true);
        if (product.getStockMinimum() == null) product.setStockMinimum(0);
        if (product.getStockMaximum() == null) product.setStockMaximum(9999);
        return enrich(productMapper.toDTO(productRepository.save(product)));
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductUpdateDTO dto) {
        Product product = findOrThrow(id);
        productMapper.updateFromDTO(dto, product);
        return enrich(productMapper.toDTO(productRepository.save(product)));
    }

    @Transactional
    public void deactivateProduct(Long id) {
        Product product = findOrThrow(id);
        product.setActif(false);
        productRepository.save(product);
    }

    public Page<ProductDTO> findAll(Pageable pageable, Long categorieId, Long fournisseurId) {
        Page<Product> products;
        if (categorieId != null) {
            products = productRepository.findByCategorieIdAndActifTrue(categorieId, pageable);
        } else if (fournisseurId != null) {
            products = productRepository.findByFournisseurPrincipalIdAndActifTrue(fournisseurId, pageable);
        } else {
            products = productRepository.findByActifTrue(pageable);
        }
        return products.map(productMapper::toDTO).map(this::enrich);
    }

    public ProductDTO findById(Long id) {
        return enrich(productMapper.toDTO(findOrThrow(id)));
    }

    public Page<ProductDTO> searchProducts(String query, Pageable pageable) {
        return productRepository
                .findByNomContainingIgnoreCaseOrReferenceContainingIgnoreCaseAndActifTrue(query, query, pageable)
                .map(productMapper::toDTO)
                .map(this::enrich);
    }

    public ProductDTO findByBarcode(String codeBarre) {
        Product product = productRepository.findByCodeBarre(codeBarre)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit introuvable avec le code-barre : " + codeBarre));
        return enrich(productMapper.toDTO(product));
    }

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id : " + id));
    }

    private ProductDTO enrich(ProductDTO dto) {
        if (dto.getCategorieId() != null) {
            categoryRepository.findById(dto.getCategorieId())
                    .ifPresent(c -> dto.setCategorieNom(c.getNom()));
        }
        if (dto.getFournisseurPrincipalId() != null) {
            supplierRepository.findById(dto.getFournisseurPrincipalId())
                    .ifPresent(s -> dto.setFournisseurNom(s.getNom()));
        }
        return dto;
    }

    private String generateUniqueReference() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        String ref;
        do {
            StringBuilder sb = new StringBuilder("PRD-");
            for (int i = 0; i < 6; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            ref = sb.toString();
        } while (productRepository.existsByReference(ref));
        return ref;
    }
}

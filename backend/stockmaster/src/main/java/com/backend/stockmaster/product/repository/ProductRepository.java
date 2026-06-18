package com.backend.stockmaster.product.repository;

import com.backend.stockmaster.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByActifTrue(Pageable pageable);
    Page<Product> findByCategorieIdAndActifTrue(Long categorieId, Pageable pageable);
    Page<Product> findByFournisseurPrincipalIdAndActifTrue(Long fournisseurId, Pageable pageable);
    boolean existsByReference(String reference);
    boolean existsByCodeBarre(String codeBarre);
    Optional<Product> findByCodeBarre(String codeBarre);
    Page<Product> findByNomContainingIgnoreCaseOrReferenceContainingIgnoreCase(String nom, String reference, Pageable pageable);
    Page<Product> findByNomContainingIgnoreCaseOrReferenceContainingIgnoreCaseAndActifTrue(String nom, String reference, Pageable pageable);
    long countByActifTrue();
    boolean existsByCategorieIdAndActifTrue(Long categorieId);
}

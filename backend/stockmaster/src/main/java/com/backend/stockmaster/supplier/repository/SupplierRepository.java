package com.backend.stockmaster.supplier.repository;

import com.backend.stockmaster.supplier.domain.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Page<Supplier> findByActifTrue(Pageable pageable);
    boolean existsByCode(String code);
    List<Supplier> findByNomContainingIgnoreCaseAndActifTrue(String nom);
    long countByActifTrue();
}

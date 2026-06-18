package com.backend.stockmaster.warehouse.repository;

import com.backend.stockmaster.warehouse.domain.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Page<Warehouse> findByActifTrue(Pageable pageable);
    List<Warehouse> findByActifTrue();
    boolean existsByCode(String code);
    long countByActifTrue();
    long count();
}

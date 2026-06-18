package com.backend.stockmaster.category.repository;

import com.backend.stockmaster.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByActifTrue();
    List<Category> findByParentIdIsNull();
    List<Category> findByParentId(Long parentId);
    boolean existsByNomIgnoreCase(String nom);
    long countByParentId(Long parentId);
    boolean existsByIdAndActifTrue(Long id);
}

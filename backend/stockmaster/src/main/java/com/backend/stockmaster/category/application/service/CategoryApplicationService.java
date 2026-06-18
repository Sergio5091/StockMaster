package com.backend.stockmaster.category.application.service;

import com.backend.stockmaster.category.application.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CategoryApplicationService {

    public CategoryDTO createCategory(CategoryCreateDTO dto) {
        // Version stub temporaire
        return CategoryDTO.builder()
                .id(1L)
                .nom(dto.getNom())
                .description(dto.getDescription())
                .parentId(dto.getParentId())
                .actif(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public CategoryDTO updateCategory(Long id, CategoryUpdateDTO dto) {
        throw new UnsupportedOperationException("À implémenter");
    }

    public void deleteCategory(Long id) {
        throw new UnsupportedOperationException("À implémenter");
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        throw new UnsupportedOperationException("À implémenter");
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> findActiveCategories() {
        // Retourner une liste vide temporairement
        return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> findSubCategories(Long parentId) {
        return new ArrayList<>();
    }
}
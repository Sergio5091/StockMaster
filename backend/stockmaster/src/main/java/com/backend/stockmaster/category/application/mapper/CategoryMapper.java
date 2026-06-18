package com.backend.stockmaster.category.application.mapper;

import com.backend.stockmaster.category.application.dto.CategoryCreateDTO;
import com.backend.stockmaster.category.application.dto.CategoryDTO;
import com.backend.stockmaster.category.application.dto.CategoryUpdateDTO;
import com.backend.stockmaster.category.domain.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDTO toDTO(Category category) {
        if (category == null) return null;
        
        return CategoryDTO.builder()
                .id(category.getId())
                .nom(category.getNom())
                .description(category.getDescription())
                .parentId(category.getParentId())
                .actif(category.isActif())
                .build();
    }

    public Category toEntity(CategoryCreateDTO dto) {
        if (dto == null) return null;
        
        return Category.builder()
                .nom(dto.getNom())
                .description(dto.getDescription())
                .parentId(dto.getParentId())
                .actif(true)
                .build();
    }

    public void updateFromDTO(CategoryUpdateDTO dto, Category category) {
        if (dto == null || category == null) return;
        
        category.setNom(dto.getNom());
        category.setDescription(dto.getDescription());
        category.setParentId(dto.getParentId());
    }
}
package com.backend.stockmaster.category.application.service;

import com.backend.stockmaster.category.application.dto.*;
import com.backend.stockmaster.category.application.mapper.CategoryMapper;
import com.backend.stockmaster.category.domain.Category;
import com.backend.stockmaster.category.repository.CategoryRepository;
import com.backend.stockmaster.core.exception.BusinessException;
import com.backend.stockmaster.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryApplicationService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryDTO createCategory(CategoryCreateDTO dto) {
        if (categoryRepository.existsByNomIgnoreCase(dto.getNom())) {
            throw new BusinessException("Une catégorie avec ce nom existe déjà");
        }

        validateParent(dto.getParentId());

        Category category = categoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(category);
        return enrich(categoryMapper.toDTO(saved));
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryUpdateDTO dto) {
        Category category = findEntityOrThrow(id);

        if (!category.getNom().equalsIgnoreCase(dto.getNom())
                && categoryRepository.existsByNomIgnoreCase(dto.getNom())) {
            throw new BusinessException("Une catégorie avec ce nom existe déjà");
        }

        validateParent(dto.getParentId());
        if (dto.getParentId() != null && dto.getParentId().equals(id)) {
            throw new BusinessException("Une catégorie ne peut pas être sa propre parente");
        }

        categoryMapper.updateFromDTO(dto, category);
        Category saved = categoryRepository.save(category);
        return enrich(categoryMapper.toDTO(saved));
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = findEntityOrThrow(id);
        category.setActif(false);
        categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        return enrich(categoryMapper.toDTO(findEntityOrThrow(id)));
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> findActiveCategories() {
        return categoryRepository.findByActifTrue().stream()
                .map(categoryMapper::toDTO)
                .map(this::enrich)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> findRootCategories() {
        return categoryRepository.findByParentIdIsNullAndActifTrue().stream()
                .map(categoryMapper::toDTO)
                .map(this::enrich)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> findSubCategories(Long parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .filter(Category::isActif)
                .map(categoryMapper::toDTO)
                .map(this::enrich)
                .collect(Collectors.toList());
    }

    private Category findEntityOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id : " + id));
    }

    private void validateParent(Long parentId) {
        if (parentId == null) {
            return;
        }
        if (!categoryRepository.existsByIdAndActifTrue(parentId)) {
            throw new ResourceNotFoundException("Catégorie parente introuvable avec l'id : " + parentId);
        }
    }

    private CategoryDTO enrich(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        if (dto.getParentId() != null) {
            categoryRepository.findById(dto.getParentId())
                    .ifPresent(parent -> dto.setParentNom(parent.getNom()));
        }
        return dto;
    }
}
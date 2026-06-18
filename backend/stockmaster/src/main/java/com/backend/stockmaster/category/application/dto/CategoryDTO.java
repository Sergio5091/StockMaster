package com.backend.stockmaster.category.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long id;
    private String nom;
    private String description;
    private Long parentId;
    private String parentNom;
    private boolean actif;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

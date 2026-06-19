package com.backend.stockmaster.product.application.mapper;

import com.backend.stockmaster.product.application.dto.ProductCreateDTO;
import com.backend.stockmaster.product.application.dto.ProductDTO;
import com.backend.stockmaster.product.application.dto.ProductUpdateDTO;
import com.backend.stockmaster.product.domain.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        return Product.builder()
                .nom(dto.getNom())
                .description(dto.getDescription())
                .categorieId(dto.getCategorieId())
                .fournisseurPrincipalId(dto.getFournisseurPrincipalId())
                .codeBarre(dto.getCodeBarre())
                .prixAchat(dto.getPrixAchat())
                .prixVente(dto.getPrixVente())
                .poidsKg(dto.getPoidsKg())
                .volumeM3(dto.getVolumeM3())
                .stockMinimum(dto.getStockMinimum())
                .stockMaximum(dto.getStockMaximum())
                .uniteMesure(dto.getUniteMesure())
                .actif(true)
                .build();
    }

    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }
        return ProductDTO.builder()
                .id(product.getId())
                .reference(product.getReference())
                .codeBarre(product.getCodeBarre())
                .nom(product.getNom())
                .description(product.getDescription())
                .categorieId(product.getCategorieId())
                .fournisseurPrincipalId(product.getFournisseurPrincipalId())
                .prixAchat(product.getPrixAchat())
                .prixVente(product.getPrixVente())
                .poidsKg(product.getPoidsKg())
                .volumeM3(product.getVolumeM3())
                .stockMinimum(product.getStockMinimum())
                .stockMaximum(product.getStockMaximum())
                .uniteMesure(product.getUniteMesure())
                .actif(product.isActif())
                .imageUrl(product.getImageUrl())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public void updateFromDTO(ProductUpdateDTO dto, Product product) {
        if (dto == null || product == null) {
            return;
        }
        product.setNom(dto.getNom());
        product.setDescription(dto.getDescription());
        product.setCategorieId(dto.getCategorieId());
        product.setFournisseurPrincipalId(dto.getFournisseurPrincipalId());
        product.setCodeBarre(dto.getCodeBarre());
        product.setPrixAchat(dto.getPrixAchat());
        product.setPrixVente(dto.getPrixVente());
        product.setPoidsKg(dto.getPoidsKg());
        product.setVolumeM3(dto.getVolumeM3());
        product.setStockMinimum(dto.getStockMinimum());
        product.setStockMaximum(dto.getStockMaximum());
        product.setUniteMesure(dto.getUniteMesure());
    }
}
package com.backend.stockmaster.supplier.application.mapper;

import com.backend.stockmaster.supplier.application.dto.SupplierCreateDTO;
import com.backend.stockmaster.supplier.application.dto.SupplierDTO;
import com.backend.stockmaster.supplier.application.dto.SupplierUpdateDTO;
import com.backend.stockmaster.supplier.domain.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public Supplier toEntity(SupplierCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        return Supplier.builder()
                .nom(dto.getNom())
                .adresse(dto.getAdresse())
                .ville(dto.getVille())
                .pays(dto.getPays())
                .telephone(dto.getTelephone())
                .email(dto.getEmail())
                .contactPrincipalNom(dto.getContactPrincipalNom())
                .contactPrincipalPrenom(dto.getContactPrincipalPrenom())
                .contactPrincipalTelephone(dto.getContactPrincipalTelephone())
                .contactPrincipalEmail(dto.getContactPrincipalEmail())
                .delaiLivraisonJours(dto.getDelaiLivraisonJours())
                .actif(true)
                .build();
    }

    public SupplierDTO toDTO(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        return SupplierDTO.builder()
                .id(supplier.getId())
                .code(supplier.getCode())
                .nom(supplier.getNom())
                .adresse(supplier.getAdresse())
                .ville(supplier.getVille())
                .pays(supplier.getPays())
                .telephone(supplier.getTelephone())
                .email(supplier.getEmail())
                .contactPrincipalNom(supplier.getContactPrincipalNom())
                .contactPrincipalPrenom(supplier.getContactPrincipalPrenom())
                .contactPrincipalTelephone(supplier.getContactPrincipalTelephone())
                .contactPrincipalEmail(supplier.getContactPrincipalEmail())
                .delaiLivraisonJours(supplier.getDelaiLivraisonJours())
                .actif(supplier.isActif())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }

    public void updateFromDTO(SupplierUpdateDTO dto, Supplier supplier) {
        if (dto == null || supplier == null) {
            return;
        }
        supplier.setNom(dto.getNom());
        supplier.setAdresse(dto.getAdresse());
        supplier.setVille(dto.getVille());
        supplier.setPays(dto.getPays());
        supplier.setTelephone(dto.getTelephone());
        supplier.setEmail(dto.getEmail());
        supplier.setContactPrincipalNom(dto.getContactPrincipalNom());
        supplier.setContactPrincipalPrenom(dto.getContactPrincipalPrenom());
        supplier.setContactPrincipalTelephone(dto.getContactPrincipalTelephone());
        supplier.setContactPrincipalEmail(dto.getContactPrincipalEmail());
        supplier.setDelaiLivraisonJours(dto.getDelaiLivraisonJours());
    }
}
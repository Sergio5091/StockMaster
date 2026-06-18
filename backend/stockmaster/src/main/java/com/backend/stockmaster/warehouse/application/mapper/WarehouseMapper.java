package com.backend.stockmaster.warehouse.application.mapper;

import com.backend.stockmaster.warehouse.application.dto.WarehouseCreateDTO;
import com.backend.stockmaster.warehouse.application.dto.WarehouseDTO;
import com.backend.stockmaster.warehouse.application.dto.WarehouseUpdateDTO;
import com.backend.stockmaster.warehouse.domain.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    public WarehouseDTO toDTO(Warehouse warehouse) {
        if (warehouse == null) return null;
        
        return WarehouseDTO.builder()
                .id(warehouse.getId())
                .code(warehouse.getCode())
                .nom(warehouse.getNom())
                .adresse(warehouse.getAdresse())
                .ville(warehouse.getVille())
                .pays(warehouse.getPays())
                .responsableId(warehouse.getResponsableId())
                .capaciteTotale(warehouse.getCapaciteTotale())
                .actif(warehouse.isActif())
                .telephone(warehouse.getTelephone())
                .email(warehouse.getEmail())
                .build();
    }

    public Warehouse toEntity(WarehouseCreateDTO dto) {
        if (dto == null) return null;
        
        return Warehouse.builder()
                .nom(dto.getNom())
                .adresse(dto.getAdresse())
                .ville(dto.getVille())
                .pays(dto.getPays())
                .responsableId(dto.getResponsableId())
                .capaciteTotale(dto.getCapaciteTotale())
                .telephone(dto.getTelephone())
                .email(dto.getEmail())
                .actif(true)
                .build();
    }

    public void updateFromDTO(WarehouseUpdateDTO dto, Warehouse warehouse) {
        if (dto == null || warehouse == null) return;
        
        warehouse.setNom(dto.getNom());
        warehouse.setAdresse(dto.getAdresse());
        warehouse.setVille(dto.getVille());
        warehouse.setPays(dto.getPays());
        warehouse.setResponsableId(dto.getResponsableId());
        warehouse.setCapaciteTotale(dto.getCapaciteTotale());
        warehouse.setTelephone(dto.getTelephone());
        warehouse.setEmail(dto.getEmail());
    }
}
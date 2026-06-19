package com.backend.stockmaster.warehouse.application.mapper;

import com.backend.stockmaster.warehouse.application.dto.WarehouseCreateDTO;
import com.backend.stockmaster.warehouse.application.dto.WarehouseDTO;
import com.backend.stockmaster.warehouse.application.dto.WarehouseUpdateDTO;
import com.backend.stockmaster.warehouse.domain.Warehouse;
import com.backend.stockmaster.zone.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WarehouseMapper {

    private final ZoneRepository zoneRepository;

    public WarehouseDTO toDTO(Warehouse warehouse) {
        if (warehouse == null)
            return null;

        double capaciteUtilisee = 0.0;
        for (var zone : zoneRepository.findByEntrepotId(warehouse.getId())) {
            Double occupation = zone.getOccupationM3();
            capaciteUtilisee += occupation != null ? occupation : 0.0;
        }
        int nbZones = (int) zoneRepository.countByEntrepotIdAndActifTrue(warehouse.getId());

        return WarehouseDTO.builder()
                .id(warehouse.getId())
                .code(warehouse.getCode())
                .nom(warehouse.getNom())
                .adresse(warehouse.getAdresse())
                .ville(warehouse.getVille())
                .pays(warehouse.getPays())
                .responsableId(warehouse.getResponsableId())
                .capaciteTotale(warehouse.getCapaciteTotale())
                .capaciteUtilisee(capaciteUtilisee)
                .nbZones(nbZones)
                .actif(warehouse.isActif())
                .telephone(warehouse.getTelephone())
                .email(warehouse.getEmail())
                .build();
    }

    public Warehouse toEntity(WarehouseCreateDTO dto) {
        if (dto == null)
            return null;

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
        if (dto == null || warehouse == null)
            return;

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
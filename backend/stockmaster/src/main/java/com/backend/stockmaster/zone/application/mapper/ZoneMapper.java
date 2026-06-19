package com.backend.stockmaster.zone.application.mapper;

import org.springframework.stereotype.Component;

import com.backend.stockmaster.zone.application.dto.ZoneCreateDTO;
import com.backend.stockmaster.zone.domain.Zone;

@Component
public class ZoneMapper {

    public Zone toEntity(ZoneCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        Double capaciteM3 = dto.getCapaciteM3() != null ? dto.getCapaciteM3().doubleValue() : 0.0;
        return Zone.builder()
                .nom(dto.getNom())
                .type(dto.getType())
                .capaciteM3(capaciteM3)
                .occupationM3(0.0d)
                .actif(true)
                .build();
    }

    public com.backend.stockmaster.zone.application.dto.ZoneDTO toDTO(Zone zone) {
        if (zone == null) {
            return null;
        }
        return com.backend.stockmaster.zone.application.dto.ZoneDTO.builder()
                .id(zone.getId())
                .code(zone.getCode())
                .nom(zone.getNom())
                .type(zone.getType())
                .entrepotId(zone.getEntrepotId())
                .capaciteM3(zone.getCapaciteM3())
                .occupationM3(zone.getOccupationM3())
                .tauxOccupation(
                        zone.getCapaciteM3() != null && zone.getCapaciteM3() > 0 && zone.getOccupationM3() != null
                                ? Math.round((zone.getOccupationM3() / zone.getCapaciteM3()) * 1000.0) / 10.0
                                : 0.0)
                .actif(zone.isActif())
                .createdAt(zone.getCreatedAt())
                .build();
    }
}
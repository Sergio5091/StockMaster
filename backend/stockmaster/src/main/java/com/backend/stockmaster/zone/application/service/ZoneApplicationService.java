package com.backend.stockmaster.zone.application.service;

import com.backend.stockmaster.core.exception.BusinessException;
import com.backend.stockmaster.core.exception.ResourceNotFoundException;
import com.backend.stockmaster.location.repository.LocationRepository;
import com.backend.stockmaster.location.domain.LocationStatus;
import com.backend.stockmaster.warehouse.repository.WarehouseRepository;
import com.backend.stockmaster.zone.application.dto.ZoneCreateDTO;
import com.backend.stockmaster.zone.application.dto.ZoneDTO;
import com.backend.stockmaster.zone.application.dto.ZoneOccupancyDTO;
import com.backend.stockmaster.zone.application.mapper.ZoneMapper;
import com.backend.stockmaster.zone.domain.Zone;
import com.backend.stockmaster.zone.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ZoneApplicationService {

    private final ZoneRepository zoneRepository;
    private final ZoneMapper zoneMapper;
    private final WarehouseRepository warehouseRepository;
    private final LocationRepository locationRepository;

    public List<ZoneDTO> findByWarehouse(Long warehouseId) {
        warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entrepôt introuvable : " + warehouseId));
        return zoneRepository.findByEntrepotIdAndActifTrue(warehouseId)
                .stream()
                .map(zoneMapper::toDTO)
                .map(this::enrich)
                .collect(Collectors.toList());
    }

    public ZoneDTO findById(Long id) {
        return enrich(zoneMapper.toDTO(findOrThrow(id)));
    }

    public ZoneOccupancyDTO getOccupancy(Long id) {
        Zone zone = findOrThrow(id);
        double taux = zone.getCapaciteM3() > 0
                ? (zone.getOccupationM3() / zone.getCapaciteM3()) * 100
                : 0.0;
        long total = locationRepository.countByZoneId(id);
        long libres = locationRepository.countByZoneIdAndStatut(id, LocationStatus.LIBRE);
        return ZoneOccupancyDTO.builder()
                .zoneId(zone.getId())
                .nom(zone.getNom())
                .capaciteM3(zone.getCapaciteM3())
                .occupationM3(zone.getOccupationM3())
                .tauxOccupation(Math.round(taux * 10.0) / 10.0)
                .nombreEmplacements(total)
                .emplacementsLibres(libres)
                .build();
    }

    @Transactional
    public ZoneDTO createZone(Long warehouseId, ZoneCreateDTO dto) {
        warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entrepôt introuvable : " + warehouseId));

        String code = generateCode(warehouseId);
        Zone zone = zoneMapper.toEntity(dto);
        zone.setCode(code);
        zone.setEntrepotId(warehouseId);
        zone.setActif(true);
        zone.setOccupationM3(0.0);
        if (zone.getCapaciteM3() == null) zone.setCapaciteM3(0.0);

        return enrich(zoneMapper.toDTO(zoneRepository.save(zone)));
    }

    @Transactional
    public void deleteZone(Long id) {
        Zone zone = findOrThrow(id);
        zone.setActif(false);
        zoneRepository.save(zone);
    }

    private Zone findOrThrow(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone introuvable avec l'id : " + id));
    }

    private ZoneDTO enrich(ZoneDTO dto) {
        if (dto.getEntrepotId() != null) {
            warehouseRepository.findById(dto.getEntrepotId())
                    .ifPresent(w -> dto.setEntrepotNom(w.getNom()));
        }
        if (dto.getCapaciteM3() != null && dto.getCapaciteM3() > 0 && dto.getOccupationM3() != null) {
            dto.setTauxOccupation(
                    Math.round((dto.getOccupationM3() / dto.getCapaciteM3()) * 1000.0) / 10.0);
        } else {
            dto.setTauxOccupation(0.0);
        }
        return dto;
    }

    private String generateCode(Long warehouseId) {
        long count = zoneRepository.countByEntrepotIdAndActifTrue(warehouseId) + 1;
        char letter = (char) ('A' + (count - 1) % 26);
        return "ZONE-" + letter;
    }
}

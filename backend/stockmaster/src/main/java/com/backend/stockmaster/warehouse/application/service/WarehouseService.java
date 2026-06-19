package com.backend.stockmaster.warehouse.application.service;

import com.backend.stockmaster.core.exception.ResourceNotFoundException;
import com.backend.stockmaster.user.repository.UserRepository;
import com.backend.stockmaster.stock.repository.StockRepository;
import com.backend.stockmaster.warehouse.application.dto.*;
import com.backend.stockmaster.warehouse.application.mapper.WarehouseMapper;
import com.backend.stockmaster.warehouse.domain.Warehouse;
import com.backend.stockmaster.warehouse.repository.WarehouseRepository;
import com.backend.stockmaster.zone.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final UserRepository userRepository;
    private final ZoneRepository zoneRepository;
    private final StockRepository stockRepository;

    @Transactional
    public WarehouseDTO createWarehouse(WarehouseCreateDTO dto) {
        String code = generateCode();
        Warehouse warehouse = warehouseMapper.toEntity(dto);
        warehouse.setCode(code);
        warehouse.setActif(true);
        return enrich(warehouseMapper.toDTO(warehouseRepository.save(warehouse)));
    }

    @Transactional
    public WarehouseDTO updateWarehouse(Long id, WarehouseUpdateDTO dto) {
        Warehouse warehouse = findOrThrow(id);
        warehouseMapper.updateFromDTO(dto, warehouse);
        return enrich(warehouseMapper.toDTO(warehouseRepository.save(warehouse)));
    }

    @Transactional
    public void deactivateWarehouse(Long id) {
        Warehouse warehouse = findOrThrow(id);
        warehouse.setActif(false);
        warehouseRepository.save(warehouse);
    }

    public Page<WarehouseDTO> findAll(Pageable pageable) {
        return warehouseRepository.findByActifTrue(pageable)
                .map(warehouseMapper::toDTO)
                .map(this::enrich);
    }

    public WarehouseDTO findById(Long id) {
        return enrich(warehouseMapper.toDTO(findOrThrow(id)));
    }

    public WarehouseStatsDTO getStats(Long id) {
        Warehouse warehouse = findOrThrow(id);
        double capaciteUtilisee = 0.0;
        for (var zone : zoneRepository.findByEntrepotId(warehouse.getId())) {
            Double occupation = zone.getOccupationM3();
            capaciteUtilisee += occupation != null ? occupation : 0.0;
        }
        long nombreZones = zoneRepository.countByEntrepotIdAndActifTrue(warehouse.getId());
        long nombreProduits = stockRepository.findByEntrepotId(warehouse.getId()).stream()
                .map(stock -> stock.getProduitId())
                .distinct()
                .count();
        return WarehouseStatsDTO.builder()
                .warehouseId(warehouse.getId())
                .nom(warehouse.getNom())
                .capaciteTotale(warehouse.getCapaciteTotale())
                .capaciteUtilisee(capaciteUtilisee)
                .tauxOccupation(warehouse.getCapaciteTotale() != null && warehouse.getCapaciteTotale() > 0
                        ? Math.round((capaciteUtilisee / warehouse.getCapaciteTotale()) * 1000.0) / 10.0
                        : 0.0)
                .nombreZones(nombreZones)
                .nombreProduits(nombreProduits)
                .build();
    }

    private Warehouse findOrThrow(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrepôt introuvable avec l'id : " + id));
    }

    private WarehouseDTO enrich(WarehouseDTO dto) {
        if (dto.getResponsableId() != null) {
            userRepository.findById(dto.getResponsableId())
                    .ifPresent(u -> dto.setResponsableUsername(u.getUsername()));
        }
        return dto;
    }

    private String generateCode() {
        long count = warehouseRepository.count() + 1;
        return String.format("ENT-%03d", count);
    }
}

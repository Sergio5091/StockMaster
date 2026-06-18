package com.backend.stockmaster.warehouse.application.service;

import com.backend.stockmaster.core.exception.ResourceNotFoundException;
import com.backend.stockmaster.warehouse.application.dto.WarehouseCreateDTO;
import com.backend.stockmaster.warehouse.application.dto.WarehouseDTO;
import com.backend.stockmaster.warehouse.application.dto.WarehouseUpdateDTO;
import com.backend.stockmaster.warehouse.application.mapper.WarehouseMapper;
import com.backend.stockmaster.warehouse.domain.Warehouse;
import com.backend.stockmaster.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WarehouseApplicationService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    public List<WarehouseDTO> findAll() {
        log.info("Récupération de tous les entrepôts");
        return warehouseRepository.findAll().stream()
                .map(warehouseMapper::toDTO)
                .collect(Collectors.toList());
    }

    public WarehouseDTO findById(Long id) {
        log.info("Récupération de l'entrepôt avec l'ID: {}", id);
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrepôt non trouvé avec l'ID: " + id));
        return warehouseMapper.toDTO(warehouse);
    }

    @Transactional
    public WarehouseDTO createWarehouse(WarehouseCreateDTO dto) {
        log.info("Création d'un nouvel entrepôt: {}", dto.getNom());
        
        // Générer le code automatiquement
        String code = generateWarehouseCode();
        
        Warehouse warehouse = warehouseMapper.toEntity(dto);
        warehouse.setCode(code);
        warehouse.setActif(true);
        
        Warehouse saved = warehouseRepository.save(warehouse);
        log.info("Entrepôt créé avec succès avec l'ID: {} et le code: {}", saved.getId(), saved.getCode());
        
        return warehouseMapper.toDTO(saved);
    }

    @Transactional
    public WarehouseDTO updateWarehouse(Long id, WarehouseUpdateDTO dto) {
        log.info("Mise à jour de l'entrepôt avec l'ID: {}", id);
        
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrepôt non trouvé avec l'ID: " + id));
        
        warehouseMapper.updateFromDTO(dto, warehouse);
        Warehouse saved = warehouseRepository.save(warehouse);
        
        log.info("Entrepôt mis à jour avec succès: {}", saved.getId());
        return warehouseMapper.toDTO(saved);
    }

    @Transactional
    public void deleteWarehouse(Long id) {
        log.info("Suppression de l'entrepôt avec l'ID: {}", id);
        
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrepôt non trouvé avec l'ID: " + id));
        
        // Soft delete - désactiver plutôt que supprimer
        warehouse.setActif(false);
        warehouseRepository.save(warehouse);
        
        log.info("Entrepôt désactivé avec succès: {}", id);
    }

    public List<WarehouseDTO> findActiveWarehouses() {
        log.info("Récupération des entrepôts actifs");
        return warehouseRepository.findByActifTrue().stream()
                .map(warehouseMapper::toDTO)
                .collect(Collectors.toList());
    }

    private String generateWarehouseCode() {
        // Générer un code au format ENT-XXX
        Long count = warehouseRepository.count() + 1;
        return String.format("ENT-%03d", count);
    }
}
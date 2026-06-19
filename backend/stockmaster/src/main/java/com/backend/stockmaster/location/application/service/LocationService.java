package com.backend.stockmaster.location.application.service;

import com.backend.stockmaster.core.exception.ResourceNotFoundException;
import com.backend.stockmaster.location.application.dto.*;
import com.backend.stockmaster.location.domain.*;
import com.backend.stockmaster.location.repository.LocationRepository;
import com.backend.stockmaster.product.repository.ProductRepository;
import com.backend.stockmaster.zone.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    private final LocationRepository locationRepository;
    private final ZoneRepository zoneRepository;
    private final ProductRepository productRepository;

    public List<LocationDTO> findByZone(Long zoneId) {
        zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone introuvable : " + zoneId));
        return locationRepository.findByZoneIdAndActifTrue(zoneId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public LocationDTO findById(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional
    public LocationDTO create(Long zoneId, LocationCreateDTO dto) {
        zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone introuvable : " + zoneId));
        long count = locationRepository.countByZoneId(zoneId) + 1;
        String code = String.format("LOC-%d-%03d", zoneId, count);
        Double capaciteKg = dto.getCapaciteKg();
        if (capaciteKg == null) {
            capaciteKg = 0.0d;
        }
        Location loc = Location.builder()
                .code(code).zoneId(zoneId)
                .rayon(dto.getRayon()).etagere(dto.getEtagere()).position(dto.getPosition())
                .capaciteKg(capaciteKg)
                .statut(LocationStatus.LIBRE).build();
        return toDTO(locationRepository.save(loc));
    }

    @Transactional
    public LocationDTO updateStatus(Long id, LocationStatus statut) {
        Location loc = findOrThrow(id);
        loc.setStatut(statut);
        return toDTO(locationRepository.save(loc));
    }

    @Transactional
    public void delete(Long id) {
        Location loc = findOrThrow(id);
        loc.setActif(false);
        locationRepository.save(loc);
    }

    private Location findOrThrow(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emplacement introuvable : " + id));
    }

    private LocationDTO toDTO(Location l) {
        LocationDTO dto = LocationDTO.builder()
                .id(l.getId()).code(l.getCode()).zoneId(l.getZoneId())
                .rayon(l.getRayon()).etagere(l.getEtagere()).position(l.getPosition())
                .capaciteKg(l.getCapaciteKg()).poidsActuel(l.getPoidsActuel())
                .statut(l.getStatut()).produitId(l.getProduitId()).build();
        zoneRepository.findById(l.getZoneId()).ifPresent(z -> dto.setZoneNom(z.getNom()));
        if (l.getProduitId() != null)
            productRepository.findById(l.getProduitId()).ifPresent(p -> dto.setProduitNom(p.getNom()));
        return dto;
    }
}

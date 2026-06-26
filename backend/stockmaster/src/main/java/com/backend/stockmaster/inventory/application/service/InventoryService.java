package com.backend.stockmaster.inventory.application.service;

import com.backend.stockmaster.category.repository.CategoryRepository;
import com.backend.stockmaster.core.exception.BusinessException;
import com.backend.stockmaster.core.exception.ResourceNotFoundException;
import com.backend.stockmaster.inventory.application.dto.*;
import com.backend.stockmaster.inventory.domain.*;
import com.backend.stockmaster.inventory.repository.InventoryRepository;
import com.backend.stockmaster.product.repository.ProductRepository;
import com.backend.stockmaster.stock.application.service.StockApplicationService;
import com.backend.stockmaster.stock.domain.MovementType;
import com.backend.stockmaster.stock.repository.StockRepository;
import com.backend.stockmaster.warehouse.repository.WarehouseRepository;
import com.backend.stockmaster.location.repository.LocationRepository;
import com.backend.stockmaster.zone.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockRepository stockRepository;
    private final StockApplicationService stockService;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final ZoneRepository zoneRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    public Page<InventoryDTO> findAll(Pageable pageable) {
        return inventoryRepository.findAll(pageable).map(this::toDTO);
    }

    public InventoryDTO findById(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional
    public InventoryDTO create(InventoryCreateDTO dto) {
        Inventory inv = Inventory.builder()
                .numero(generateNumero())
                .entrepotId(dto.getEntrepotId())
                .zoneId(dto.getZoneId())
                .categorieId(dto.getCategorieId())
                .type(dto.getType())
                .datePlanifiee(dto.getDatePlanifiee())
                .note(dto.getNote())
                .statut(InventoryStatus.PLANIFIE)
                .creePar(currentUser())
                .build();
        return toDTO(inventoryRepository.save(inv));
    }

    @Transactional
    public InventoryDTO start(Long id) {
        Inventory inv = findOrThrow(id);
        if (inv.getStatut() != InventoryStatus.PLANIFIE)
            throw new BusinessException("Seul un inventaire planifié peut être démarré.");

        // Si zoneId défini : ne charger que les produits ayant des emplacements dans cette zone
        java.util.Set<Long> produitsDansZone = null;
        if (inv.getZoneId() != null) {
            produitsDansZone = locationRepository.findByZoneIdAndActifTrue(inv.getZoneId()).stream()
                    .filter(l -> l.getProduitId() != null)
                    .map(com.backend.stockmaster.location.domain.Location::getProduitId)
                    .collect(java.util.stream.Collectors.toSet());
        }
        final java.util.Set<Long> zoneFilter = produitsDansZone;

        stockRepository.findByEntrepotId(inv.getEntrepotId()).forEach(s -> {
            productRepository.findById(s.getProduitId()).ifPresent(p -> {
                // Filtre par catégorie
                if (inv.getCategorieId() != null && !inv.getCategorieId().equals(p.getCategorieId())) return;
                // Filtre par zone
                if (zoneFilter != null && !zoneFilter.contains(p.getId())) return;
                addLine(inv, s.getProduitId(), s.getQuantiteDisponible());
            });
        });

        inv.setStatut(InventoryStatus.EN_COURS);
        inv.setDateDebut(LocalDate.now());
        return toDTO(inventoryRepository.save(inv));
    }

    @Transactional
    public InventoryDTO count(Long id, InventoryCountDTO dto) {
        Inventory inv = findOrThrow(id);
        if (inv.getStatut() != InventoryStatus.EN_COURS)
            throw new BusinessException("L'inventaire doit être en cours pour saisir un comptage.");
        InventoryLine line = inv.getLignes().stream()
                .filter(l -> l.getProduitId().equals(dto.getProduitId()))
                .findFirst()
                .orElseGet(() -> {
                    InventoryLine nl = InventoryLine.builder().inventaire(inv).produitId(dto.getProduitId()).build();
                    inv.getLignes().add(nl);
                    return nl;
                });
        line.setQuantiteComptee(dto.getQuantiteComptee());
        int theorique = line.getQuantiteTheorique() != null ? line.getQuantiteTheorique() : 0;
        line.setEcart(dto.getQuantiteComptee() - theorique);
        line.setNote(dto.getNote());
        return toDTO(inventoryRepository.save(inv));
    }

    @Transactional
    public InventoryDTO validate(Long id) {
        Inventory inv = findOrThrow(id);
        if (inv.getStatut() != InventoryStatus.EN_COURS)
            throw new BusinessException("L'inventaire doit être en cours pour être validé.");
        // Ajuster le stock pour chaque écart
        inv.getLignes().stream().filter(l -> l.getEcart() != 0).forEach(l -> {
            if (l.getEcart() > 0)
                stockService.addStock(l.getProduitId(), inv.getEntrepotId(), l.getEcart(),
                        MovementType.AJUSTEMENT_INVENTAIRE, inv.getNumero(), null, "Ajustement inventaire " + inv.getNumero());
            else
                stockService.removeStock(l.getProduitId(), inv.getEntrepotId(), Math.abs(l.getEcart()),
                        MovementType.AJUSTEMENT_INVENTAIRE, inv.getNumero(), null, "Ajustement inventaire " + inv.getNumero());
            l.setAjuste(true);
        });
        inv.setStatut(InventoryStatus.TERMINE);
        inv.setDateFin(LocalDate.now());
        inv.setValideePar(currentUser());
        return toDTO(inventoryRepository.save(inv));
    }

    @Transactional
    public InventoryDTO cancel(Long id) {
        Inventory inv = findOrThrow(id);
        if (inv.getStatut() == InventoryStatus.TERMINE)
            throw new BusinessException("Un inventaire terminé ne peut pas être annulé.");
        inv.setStatut(InventoryStatus.ANNULE);
        return toDTO(inventoryRepository.save(inv));
    }

    private void addLine(Inventory inv, Long produitId, int qte) {
        inv.getLignes().add(InventoryLine.builder()
                .inventaire(inv).produitId(produitId)
                .quantiteTheorique(qte).build());
    }

    private Inventory findOrThrow(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventaire introuvable : " + id));
    }

    private String generateNumero() {
        int year = LocalDate.now().getYear();
        long count = inventoryRepository.count() + 1;
        return String.format("INV-%d-%04d", year, count);
    }

    private String currentUser() {
        try { return SecurityContextHolder.getContext().getAuthentication().getName(); }
        catch (Exception e) { return "system"; }
    }

    private InventoryDTO toDTO(Inventory i) {
        InventoryDTO dto = InventoryDTO.builder()
                .id(i.getId()).numero(i.getNumero())
                .entrepotId(i.getEntrepotId()).zoneId(i.getZoneId())
                .categorieId(i.getCategorieId())
                .type(i.getType()).statut(i.getStatut())
                .datePlanifiee(i.getDatePlanifiee()).dateDebut(i.getDateDebut())
                .dateFin(i.getDateFin()).note(i.getNote())
                .creePar(i.getCreePar()).valideePar(i.getValideePar())
                .nbLignes(i.getLignes().size())
                .nbEcarts((int) i.getLignes().stream().filter(l -> l.getEcart() != 0).count())
                .build();
        warehouseRepository.findById(i.getEntrepotId()).ifPresent(w -> dto.setEntrepotNom(w.getNom()));
        if (i.getZoneId() != null)
            zoneRepository.findById(i.getZoneId()).ifPresent(z -> dto.setZoneNom(z.getNom()));
        if (i.getCategorieId() != null)
            categoryRepository.findById(i.getCategorieId()).ifPresent(c -> dto.setCategorieNom(c.getNom()));
        dto.setLignes(i.getLignes().stream().map(l -> {
            InventoryLineDTO ld = InventoryLineDTO.builder()
                    .id(l.getId()).produitId(l.getProduitId())
                    .quantiteTheorique(l.getQuantiteTheorique())
                    .quantiteComptee(l.getQuantiteComptee())
                    .ecart(l.getEcart()).ajuste(l.isAjuste())
                    .note(l.getNote()).build();
            productRepository.findById(l.getProduitId()).ifPresent(p -> {
                ld.setProduitNom(p.getNom());
                ld.setProduitRef(p.getReference());
            });
            return ld;
        }).collect(Collectors.toList()));
        return dto;
    }
}

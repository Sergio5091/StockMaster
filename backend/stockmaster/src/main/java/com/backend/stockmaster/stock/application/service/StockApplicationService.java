package com.backend.stockmaster.stock.application.service;

import com.backend.stockmaster.core.exception.BusinessException;
import com.backend.stockmaster.product.repository.ProductRepository;
import com.backend.stockmaster.stock.application.dto.*;
import com.backend.stockmaster.stock.domain.*;
import com.backend.stockmaster.stock.repository.*;
import com.backend.stockmaster.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockApplicationService {

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Transactional
    public void addStock(Long produitId, Long entrepotId, Integer quantite,
                         MovementType type, String reference, Object ignored, String note) {
        Stock stock = stockRepository.findByProduitIdAndEntrepotId(produitId, entrepotId)
                .orElseGet(() -> Stock.builder().produitId(produitId).entrepotId(entrepotId).build());
        int avant = stock.getQuantiteDisponible();
        stock.setQuantiteDisponible(avant + quantite);
        stockRepository.save(stock);
        stockMovementRepository.save(StockMovement.builder()
                .type(type).produitId(produitId)
                .entrepotDestinationId(entrepotId)
                .quantite(quantite).quantiteAvant(avant).quantiteApres(stock.getQuantiteDisponible())
                .referenceDocument(reference).note(note).build());
    }

    @Transactional
    public void removeStock(Long produitId, Long entrepotId, Integer quantite,
                            MovementType type, String reference, Object ignored, String note) {
        Stock stock = stockRepository.findByProduitIdAndEntrepotId(produitId, entrepotId)
                .orElseThrow(() -> new BusinessException("Stock introuvable pour ce produit/entrepôt"));
        if (stock.getQuantiteDisponible() < quantite)
            throw new BusinessException("Stock insuffisant : disponible=" + stock.getQuantiteDisponible() + ", demandé=" + quantite);
        int avant = stock.getQuantiteDisponible();
        stock.setQuantiteDisponible(avant - quantite);
        stockRepository.save(stock);
        stockMovementRepository.save(StockMovement.builder()
                .type(type).produitId(produitId)
                .entrepotSourceId(entrepotId)
                .quantite(quantite).quantiteAvant(avant).quantiteApres(stock.getQuantiteDisponible())
                .referenceDocument(reference).note(note).build());
    }

    public StockDTO findByProduitAndEntrepot(Long produitId, Long entrepotId) {
        return stockRepository.findByProduitIdAndEntrepotId(produitId, entrepotId)
                .map(this::toDTO).orElse(null);
    }

    public Page<StockDTO> findAll(Pageable pageable) {
        return stockRepository.findAll(pageable).map(this::toDTO);
    }

    public List<StockDTO> findByWarehouse(Long warehouseId) {
        return stockRepository.findByEntrepotId(warehouseId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public Page<StockMovementDTO> findMovements(Pageable pageable) {
        return stockMovementRepository.findAll(pageable).map(this::toMovementDTO);
    }

    public Page<StockMovementDTO> findMovementsByProduit(Long produitId, Pageable pageable) {
        return stockMovementRepository.findByProduitId(produitId, pageable).map(this::toMovementDTO);
    }

    @Transactional
    public void updateStock(Long produitId, Long entrepotId, int quantite, String reference, String note) {
        addStock(produitId, entrepotId, quantite, MovementType.AJUSTEMENT_INVENTAIRE, reference, null, note);
    }

    @Transactional
    public void decreaseStock(Long produitId, Long entrepotId, int quantite, String reference, String note) {
        removeStock(produitId, entrepotId, quantite, MovementType.SORTIE, reference, null, note);
    }

    /**
     * Ajustement manuel : quantite > 0 = entrée, quantite < 0 = sortie.
     * Le type peut être forcé explicitement via le request.
     */
    @Transactional
    public StockMovementDTO ajusterStock(StockUpdateRequest req) {
        if (req.getQuantite() == null || req.getQuantite() == 0)
            throw new BusinessException("La quantité d'ajustement ne peut pas être zéro");

        MovementType type = req.getType();
        if (type == null) {
            type = req.getQuantite() > 0 ? MovementType.ENTREE : MovementType.SORTIE;
        }

        int qte = Math.abs(req.getQuantite());
        String reference = "AJUST-" + System.currentTimeMillis();

        if (req.getQuantite() > 0) {
            addStock(req.getProduitId(), req.getEntrepotId(), qte, type, reference, null, req.getJustification());
        } else {
            removeStock(req.getProduitId(), req.getEntrepotId(), qte, type, reference, null, req.getJustification());
        }

        return stockMovementRepository
                .findByReferenceDocument(reference)
                .stream()
                .findFirst()
                .map(this::toMovementDTO)
                .orElseThrow(() -> new BusinessException("Erreur lors de la création du mouvement"));
    }

    public Page<StockMovementDTO> findMovementsByEntrepot(Long entrepotId, Pageable pageable) {
        return stockMovementRepository
                .findByEntrepotSourceIdOrEntrepotDestinationId(entrepotId, entrepotId, pageable)
                .map(this::toMovementDTO);
    }

    public Page<StockMovementDTO> findMovementsByType(MovementType type, Pageable pageable) {
        return stockMovementRepository.findByType(type, pageable).map(this::toMovementDTO);
    }

    public Page<StockMovementDTO> findMovementsByPeriode(LocalDate debut, LocalDate fin, Pageable pageable) {
        LocalDateTime from = debut.atStartOfDay();
        LocalDateTime to = fin.atTime(23, 59, 59);
        return stockMovementRepository.findByPeriode(from, to, pageable).map(this::toMovementDTO);
    }

    private StockDTO toDTO(Stock s) {
        StockDTO dto = StockDTO.builder()
                .id(s.getId()).produitId(s.getProduitId())
                .entrepotId(s.getEntrepotId())
                .quantiteDisponible(s.getQuantiteDisponible())
                .quantiteReservee(s.getQuantiteReservee())
                .quantiteEnTransit(s.getQuantiteEnTransit())
                .build();
        productRepository.findById(s.getProduitId()).ifPresent(p -> {
            dto.setProduitRef(p.getReference());
            dto.setProduitNom(p.getNom());
            dto.setStockMinimum(p.getStockMinimum());
            dto.setStockMaximum(p.getStockMaximum());
            int q = s.getQuantiteDisponible();
            if (q <= 0) dto.setStatut("critical");
            else if (q <= p.getStockMinimum()) dto.setStatut("low");
            else if (q >= p.getStockMaximum()) dto.setStatut("excess");
            else dto.setStatut("normal");
        });
        warehouseRepository.findById(s.getEntrepotId()).ifPresent(w -> {
            dto.setEntrepotCode(w.getCode());
            dto.setEntrepotNom(w.getNom());
        });
        return dto;
    }

    private StockMovementDTO toMovementDTO(StockMovement m) {
        StockMovementDTO dto = StockMovementDTO.builder()
                .id(m.getId()).type(m.getType()).produitId(m.getProduitId())
                .entrepotSourceId(m.getEntrepotSourceId())
                .entrepotDestinationId(m.getEntrepotDestinationId())
                .quantite(m.getQuantite()).quantiteAvant(m.getQuantiteAvant())
                .quantiteApres(m.getQuantiteApres())
                .referenceDocument(m.getReferenceDocument())
                .note(m.getNote()).createdAt(m.getCreatedAt()).build();
        productRepository.findById(m.getProduitId()).ifPresent(p -> {
            dto.setProduitRef(p.getReference());
            dto.setProduitNom(p.getNom());
        });
        if (m.getEntrepotSourceId() != null)
            warehouseRepository.findById(m.getEntrepotSourceId()).ifPresent(w -> dto.setEntrepotSourceNom(w.getNom()));
        if (m.getEntrepotDestinationId() != null)
            warehouseRepository.findById(m.getEntrepotDestinationId()).ifPresent(w -> dto.setEntrepotDestinationNom(w.getNom()));
        return dto;
    }
}

package com.backend.stockmaster.receipt.application.service;

import com.backend.stockmaster.core.exception.BusinessException;
import com.backend.stockmaster.core.exception.ResourceNotFoundException;
import com.backend.stockmaster.product.repository.ProductRepository;
import com.backend.stockmaster.receipt.application.dto.*;
import com.backend.stockmaster.receipt.domain.*;
import com.backend.stockmaster.receipt.repository.GoodsReceiptRepository;
import com.backend.stockmaster.stock.application.service.StockApplicationService;
import com.backend.stockmaster.stock.domain.MovementType;
import com.backend.stockmaster.supplier.repository.SupplierRepository;
import com.backend.stockmaster.warehouse.repository.WarehouseRepository;
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
public class GoodsReceiptService {

    private final GoodsReceiptRepository receiptRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockApplicationService stockService;

    public Page<ReceiptDTO> findAll(Pageable pageable) {
        return receiptRepository.findAll(pageable).map(this::toDTO);
    }

    public ReceiptDTO findById(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional
    public ReceiptDTO create(ReceiptCreateDTO dto) {
        String numero = generateNumero();
        String user = currentUser();

        GoodsReceipt receipt = GoodsReceipt.builder()
                .numero(numero)
                .fournisseurId(dto.getFournisseurId())
                .entrepotId(dto.getEntrepotId())
                .commandeFournisseurId(dto.getCommandeFournisseurId())
                .statut(ReceiptStatus.BROUILLON)
                .dateReception(dto.getDateReception())
                .note(dto.getNote())
                .creePar(user)
                .build();

        if (dto.getLignes() != null) {
            dto.getLignes().forEach(l -> {
                GoodsReceiptLine line = GoodsReceiptLine.builder()
                        .bonReception(receipt)
                        .produitId(l.getProduitId())
                        .quantiteAttendue(l.getQuantiteAttendue() != null ? l.getQuantiteAttendue() : 0)
                        .quantiteRecue(l.getQuantiteRecue())
                        .qualiteOk(l.isQualiteOk())
                        .noteQualite(l.getNoteQualite())
                        .build();
                receipt.getLignes().add(line);
            });
        }
        return toDTO(receiptRepository.save(receipt));
    }

    @Transactional
    public ReceiptDTO submit(Long id) {
        GoodsReceipt receipt = findOrThrow(id);
        if (receipt.getStatut() != ReceiptStatus.BROUILLON)
            throw new BusinessException("Seul un brouillon peut être soumis.");
        receipt.setStatut(ReceiptStatus.EN_ATTENTE_VALIDATION);
        return toDTO(receiptRepository.save(receipt));
    }

    @Transactional
    public ReceiptDTO validate(Long id) {
        GoodsReceipt receipt = findOrThrow(id);
        if (receipt.getStatut() != ReceiptStatus.EN_ATTENTE_VALIDATION)
            throw new BusinessException("Seul un bon en attente peut être validé.");

        String user = currentUser();
        receipt.setStatut(ReceiptStatus.VALIDE);
        receipt.setValideePar(user);
        receipt.setDateValidation(LocalDate.now());

        // Mise à jour du stock pour chaque ligne validée
        receipt.getLignes().forEach(line -> {
            if (line.isQualiteOk() && line.getQuantiteRecue() > 0) {
                stockService.addStock(
                        line.getProduitId(), receipt.getEntrepotId(),
                        line.getQuantiteRecue(), MovementType.ENTREE,
                        receipt.getNumero(), null, "Bon de réception " + receipt.getNumero());
            }
        });

        return toDTO(receiptRepository.save(receipt));
    }

    @Transactional
    public ReceiptDTO reject(Long id) {
        GoodsReceipt receipt = findOrThrow(id);
        if (receipt.getStatut() != ReceiptStatus.EN_ATTENTE_VALIDATION)
            throw new BusinessException("Seul un bon en attente peut être rejeté.");
        receipt.setStatut(ReceiptStatus.REJETE);
        return toDTO(receiptRepository.save(receipt));
    }

    private GoodsReceipt findOrThrow(Long id) {
        return receiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bon de réception introuvable : " + id));
    }

    private String generateNumero() {
        int year = LocalDate.now().getYear();
        long count = receiptRepository.count() + 1;
        return String.format("BR-%d-%04d", year, count);
    }

    private String currentUser() {
        try { return SecurityContextHolder.getContext().getAuthentication().getName(); }
        catch (Exception e) { return "system"; }
    }

    private ReceiptDTO toDTO(GoodsReceipt r) {
        ReceiptDTO dto = ReceiptDTO.builder()
                .id(r.getId()).numero(r.getNumero())
                .fournisseurId(r.getFournisseurId())
                .entrepotId(r.getEntrepotId())
                .commandeFournisseurId(r.getCommandeFournisseurId())
                .statut(r.getStatut())
                .dateReception(r.getDateReception())
                .note(r.getNote()).creePar(r.getCreePar())
                .valideePar(r.getValideePar())
                .dateValidation(r.getDateValidation())
                .build();

        supplierRepository.findById(r.getFournisseurId())
                .ifPresent(s -> dto.setFournisseurNom(s.getNom()));
        warehouseRepository.findById(r.getEntrepotId())
                .ifPresent(w -> dto.setEntrepotNom(w.getNom()));

        dto.setLignes(r.getLignes().stream().map(l -> {
            ReceiptLineDTO ld = ReceiptLineDTO.builder()
                    .id(l.getId()).produitId(l.getProduitId())
                    .quantiteAttendue(l.getQuantiteAttendue())
                    .quantiteRecue(l.getQuantiteRecue())
                    .qualiteOk(l.isQualiteOk())
                    .noteQualite(l.getNoteQualite()).build();
            productRepository.findById(l.getProduitId())
                    .ifPresent(p -> ld.setProduitNom(p.getNom()));
            return ld;
        }).collect(Collectors.toList()));

        return dto;
    }
}

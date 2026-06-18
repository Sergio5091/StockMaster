package com.backend.stockmaster.transfer.application.service;

import com.backend.stockmaster.core.exception.BusinessException;
import com.backend.stockmaster.core.exception.ResourceNotFoundException;
import com.backend.stockmaster.product.repository.ProductRepository;
import com.backend.stockmaster.stock.application.service.StockApplicationService;
import com.backend.stockmaster.stock.domain.MovementType;
import com.backend.stockmaster.transfer.application.dto.*;
import com.backend.stockmaster.transfer.domain.*;
import com.backend.stockmaster.transfer.repository.TransferRepository;
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
public class TransferService {

    private final TransferRepository transferRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockApplicationService stockService;

    public Page<TransferDTO> findAll(Pageable pageable) {
        return transferRepository.findAll(pageable).map(this::toDTO);
    }

    public TransferDTO findById(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional
    public TransferDTO create(TransferCreateDTO dto) {
        if (dto.getEntrepotSourceId().equals(dto.getEntrepotDestinationId()))
            throw new BusinessException("La source et la destination doivent être différentes.");

        Transfer transfer = Transfer.builder()
                .numero(generateNumero())
                .entrepotSourceId(dto.getEntrepotSourceId())
                .entrepotDestinationId(dto.getEntrepotDestinationId())
                .statut(TransferStatus.BROUILLON)
                .note(dto.getNote())
                .creePar(currentUser())
                .build();

        dto.getLignes().forEach(l -> transfer.getLignes().add(
                TransferLine.builder().transfer(transfer)
                        .produitId(l.getProduitId())
                        .quantiteDemandee(l.getQuantiteDemandee())
                        .build()));

        return toDTO(transferRepository.save(transfer));
    }

    @Transactional
    public TransferDTO ship(Long id) {
        Transfer transfer = findOrThrow(id);
        if (transfer.getStatut() != TransferStatus.BROUILLON)
            throw new BusinessException("Seul un brouillon peut être expédié.");

        // Déduire du stock source (en transit)
        transfer.getLignes().forEach(l ->
            stockService.removeStock(l.getProduitId(), transfer.getEntrepotSourceId(),
                    l.getQuantiteDemandee(), MovementType.TRANSFERT_SORTANT,
                    transfer.getNumero(), null, "Transfert sortant " + transfer.getNumero()));

        transfer.setStatut(TransferStatus.EXPEDIE);
        transfer.setDateExpedition(LocalDate.now());
        transfer.setExpedieePar(currentUser());
        return toDTO(transferRepository.save(transfer));
    }

    @Transactional
    public TransferDTO receive(Long id) {
        Transfer transfer = findOrThrow(id);
        if (transfer.getStatut() != TransferStatus.EXPEDIE)
            throw new BusinessException("Seul un transfert expédié peut être réceptionné.");

        // Ajouter au stock destination
        transfer.getLignes().forEach(l -> {
            stockService.addStock(l.getProduitId(), transfer.getEntrepotDestinationId(),
                    l.getQuantiteDemandee(), MovementType.TRANSFERT_ENTRANT,
                    transfer.getNumero(), null, "Transfert entrant " + transfer.getNumero());
            l.setQuantiteRecue(l.getQuantiteDemandee());
        });

        transfer.setStatut(TransferStatus.RECU);
        transfer.setDateReception(LocalDate.now());
        transfer.setRecuePar(currentUser());
        return toDTO(transferRepository.save(transfer));
    }

    @Transactional
    public TransferDTO cancel(Long id) {
        Transfer transfer = findOrThrow(id);
        if (transfer.getStatut() == TransferStatus.RECU)
            throw new BusinessException("Un transfert reçu ne peut pas être annulé.");
        // Si expédié, on recrédite la source
        if (transfer.getStatut() == TransferStatus.EXPEDIE) {
            transfer.getLignes().forEach(l ->
                stockService.addStock(l.getProduitId(), transfer.getEntrepotSourceId(),
                        l.getQuantiteDemandee(), MovementType.AJUSTEMENT_INVENTAIRE,
                        transfer.getNumero(), null, "Annulation transfert " + transfer.getNumero()));
        }
        transfer.setStatut(TransferStatus.ANNULE);
        return toDTO(transferRepository.save(transfer));
    }

    private Transfer findOrThrow(Long id) {
        return transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfert introuvable : " + id));
    }

    private String generateNumero() {
        int year = LocalDate.now().getYear();
        long count = transferRepository.count() + 1;
        return String.format("TRF-%d-%04d", year, count);
    }

    private String currentUser() {
        try { return SecurityContextHolder.getContext().getAuthentication().getName(); }
        catch (Exception e) { return "system"; }
    }

    private TransferDTO toDTO(Transfer t) {
        TransferDTO dto = TransferDTO.builder()
                .id(t.getId()).numero(t.getNumero())
                .entrepotSourceId(t.getEntrepotSourceId())
                .entrepotDestinationId(t.getEntrepotDestinationId())
                .statut(t.getStatut()).dateExpedition(t.getDateExpedition())
                .dateReception(t.getDateReception()).creePar(t.getCreePar())
                .expedieePar(t.getExpedieePar()).recuePar(t.getRecuePar())
                .note(t.getNote()).build();

        warehouseRepository.findById(t.getEntrepotSourceId())
                .ifPresent(w -> dto.setEntrepotSourceNom(w.getNom()));
        warehouseRepository.findById(t.getEntrepotDestinationId())
                .ifPresent(w -> dto.setEntrepotDestinationNom(w.getNom()));

        dto.setLignes(t.getLignes().stream().map(l -> {
            TransferLineDTO ld = TransferLineDTO.builder()
                    .id(l.getId()).produitId(l.getProduitId())
                    .quantiteDemandee(l.getQuantiteDemandee())
                    .quantiteRecue(l.getQuantiteRecue()).build();
            productRepository.findById(l.getProduitId())
                    .ifPresent(p -> ld.setProduitNom(p.getNom()));
            return ld;
        }).collect(Collectors.toList()));

        return dto;
    }
}

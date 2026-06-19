package com.backend.stockmaster.purchaseorder.application.service;

import com.backend.stockmaster.core.exception.BusinessException;
import com.backend.stockmaster.core.exception.ResourceNotFoundException;
import com.backend.stockmaster.product.repository.ProductRepository;
import com.backend.stockmaster.purchaseorder.application.dto.*;
import com.backend.stockmaster.purchaseorder.domain.*;
import com.backend.stockmaster.purchaseorder.repository.PurchaseOrderRepository;
import com.backend.stockmaster.supplier.repository.SupplierRepository;
import com.backend.stockmaster.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseOrderService {

    private final PurchaseOrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;

    public Page<PODTO> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toDTO);
    }

    public PODTO findById(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional
    public PODTO create(POCreateDTO dto) {
        PurchaseOrder order = PurchaseOrder.builder()
                .numero(generateNumero())
                .fournisseurId(dto.getFournisseurId())
                .entrepotDestinationId(dto.getEntrepotDestinationId())
                .dateCommande(dto.getDateCommande() != null ? dto.getDateCommande() : LocalDate.now())
                .dateLivraisonPrevue(dto.getDateLivraisonPrevue())
                .note(dto.getNote())
                .statut(POStatus.BROUILLON)
                .creePar(currentUser())
                .build();
        if (dto.getLignes() != null) {
            dto.getLignes().forEach(l -> {
                BigDecimal montant = l.getPrixUnitaire().multiply(BigDecimal.valueOf(l.getQuantiteCommandee()));
                order.getLignes().add(PurchaseOrderLine.builder()
                        .commande(order).produitId(l.getProduitId())
                        .quantiteCommandee(l.getQuantiteCommandee())
                        .prixUnitaire(l.getPrixUnitaire())
                        .montantLigne(montant).build());
            });
            BigDecimal total = order.getLignes().stream()
                    .map(PurchaseOrderLine::getMontantLigne)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setMontantTotal(total);
        }
        return toDTO(orderRepository.save(order));
    }

    @Transactional
    public PODTO validate(Long id) {
        PurchaseOrder order = findOrThrow(id);
        if (order.getStatut() != POStatus.BROUILLON)
            throw new BusinessException("Seul un brouillon peut être validé.");
        order.setStatut(POStatus.VALIDEE);
        order.setValideeePar(currentUser());
        return toDTO(orderRepository.save(order));
    }

    @Transactional
    public PODTO send(Long id) {
        PurchaseOrder order = findOrThrow(id);
        if (order.getStatut() != POStatus.VALIDEE)
            throw new BusinessException("Seul une commande validée peut être envoyée.");
        order.setStatut(POStatus.ENVOYEE);
        return toDTO(orderRepository.save(order));
    }

    @Transactional
    public PODTO markDelivered(Long id) {
        PurchaseOrder order = findOrThrow(id);
        order.setStatut(POStatus.LIVREE);
        order.setDateLivraisonReelle(LocalDate.now());
        return toDTO(orderRepository.save(order));
    }

    @Transactional
    public PODTO cancel(Long id) {
        PurchaseOrder order = findOrThrow(id);
        if (order.getStatut() == POStatus.LIVREE)
            throw new BusinessException("Une commande livrée ne peut pas être annulée.");
        order.setStatut(POStatus.ANNULEE);
        return toDTO(orderRepository.save(order));
    }

    private PurchaseOrder findOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable : " + id));
    }

    private String generateNumero() {
        int year = LocalDate.now().getYear();
        long count = orderRepository.count() + 1;
        return String.format("CF-%d-%04d", year, count);
    }

    private String currentUser() {
        try { return SecurityContextHolder.getContext().getAuthentication().getName(); }
        catch (Exception e) { return "system"; }
    }

    private PODTO toDTO(PurchaseOrder o) {
        PODTO dto = PODTO.builder()
                .id(o.getId()).numero(o.getNumero())
                .fournisseurId(o.getFournisseurId())
                .entrepotDestinationId(o.getEntrepotDestinationId())
                .statut(o.getStatut())
                .dateCommande(o.getDateCommande())
                .dateLivraisonPrevue(o.getDateLivraisonPrevue())
                .dateLivraisonReelle(o.getDateLivraisonReelle())
                .montantTotal(o.getMontantTotal())
                .note(o.getNote()).creePar(o.getCreePar())
                .valideeePar(o.getValideeePar()).build();
        supplierRepository.findById(o.getFournisseurId())
                .ifPresent(s -> dto.setFournisseurNom(s.getNom()));
        warehouseRepository.findById(o.getEntrepotDestinationId())
                .ifPresent(w -> dto.setEntrepotNom(w.getNom()));
        dto.setLignes(o.getLignes().stream().map(l -> {
            POLineDTO ld = POLineDTO.builder()
                    .id(l.getId()).produitId(l.getProduitId())
                    .quantiteCommandee(l.getQuantiteCommandee())
                    .quantiteRecue(l.getQuantiteRecue())
                    .prixUnitaire(l.getPrixUnitaire())
                    .montantLigne(l.getMontantLigne()).build();
            productRepository.findById(l.getProduitId())
                    .ifPresent(p -> ld.setProduitNom(p.getNom()));
            return ld;
        }).collect(Collectors.toList()));
        return dto;
    }
}

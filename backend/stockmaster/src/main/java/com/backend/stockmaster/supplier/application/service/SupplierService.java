package com.backend.stockmaster.supplier.application.service;

import com.backend.stockmaster.core.exception.ResourceNotFoundException;
import com.backend.stockmaster.purchaseorder.domain.POStatus;
import com.backend.stockmaster.purchaseorder.domain.PurchaseOrder;
import com.backend.stockmaster.purchaseorder.repository.PurchaseOrderRepository;
import com.backend.stockmaster.supplier.application.dto.SupplierCreateDTO;
import com.backend.stockmaster.supplier.application.dto.SupplierDTO;
import com.backend.stockmaster.supplier.application.dto.SupplierUpdateDTO;
import com.backend.stockmaster.supplier.application.mapper.SupplierMapper;
import com.backend.stockmaster.supplier.domain.Supplier;
import com.backend.stockmaster.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Transactional
    public SupplierDTO createSupplier(SupplierCreateDTO dto) {
        String code = generateCode();
        Supplier supplier = supplierMapper.toEntity(dto);
        supplier.setCode(code);
        supplier.setActif(true);
        return enrich(supplierMapper.toDTO(supplierRepository.save(supplier)));
    }

    @Transactional
    public SupplierDTO updateSupplier(Long id, SupplierUpdateDTO dto) {
        Supplier supplier = findOrThrow(id);
        supplierMapper.updateFromDTO(dto, supplier);
        return enrich(supplierMapper.toDTO(supplierRepository.save(supplier)));
    }

    @Transactional
    public void deactivateSupplier(Long id) {
        Supplier supplier = findOrThrow(id);
        supplier.setActif(false);
        supplierRepository.save(supplier);
    }

    public Page<SupplierDTO> findAll(Pageable pageable) {
        return supplierRepository.findByActifTrue(pageable)
                .map(supplierMapper::toDTO)
                .map(this::enrich);
    }

    public SupplierDTO findById(Long id) {
        return enrich(supplierMapper.toDTO(findOrThrow(id)));
    }

    private Supplier findOrThrow(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur introuvable avec l'id : " + id));
    }

    private String generateCode() {
        long count = supplierRepository.count() + 1;
        return String.format("SUP-%03d", count);
    }

    /**
     * Enrichit le DTO avec les stats calculées depuis les commandes fournisseurs :
     * - commandesTotal : nombre de commandes passées
     * - tauxRespectDelai : % de commandes livrées à temps
     * - montantTotal : somme des montants de toutes les commandes
     */
    private SupplierDTO enrich(SupplierDTO dto) {
        try {
            List<PurchaseOrder> orders = purchaseOrderRepository
                    .findByFournisseurId(dto.getId(), Pageable.unpaged())
                    .getContent();

            long total = orders.size();
            dto.setCommandesTotal(total);

            if (total > 0) {
                // Montant total de toutes les commandes
                double montant = orders.stream()
                        .filter(o -> o.getMontantTotal() != null)
                        .mapToDouble(o -> o.getMontantTotal().doubleValue())
                        .sum();
                dto.setMontantTotal(montant);

                // Taux de respect des délais :
                // = commandes livrées avant ou à la date prévue / commandes ayant une date prévue et livrées
                long livreesAvecDate = orders.stream()
                        .filter(o -> o.getStatut() == POStatus.LIVREE
                                && o.getDateLivraisonPrevue() != null
                                && o.getDateLivraisonReelle() != null)
                        .count();
                long livratesATemps = orders.stream()
                        .filter(o -> o.getStatut() == POStatus.LIVREE
                                && o.getDateLivraisonPrevue() != null
                                && o.getDateLivraisonReelle() != null
                                && !o.getDateLivraisonReelle().isAfter(o.getDateLivraisonPrevue()))
                        .count();

                double taux = livreesAvecDate > 0
                        ? Math.round((livratesATemps * 100.0) / livreesAvecDate * 10) / 10.0
                        : 0.0;
                dto.setTauxRespectDelai(taux);
            } else {
                dto.setMontantTotal(0.0);
                dto.setTauxRespectDelai(0.0);
            }
        } catch (Exception ignored) {
            // Si le calcul échoue, on laisse les valeurs par défaut (0)
        }
        return dto;
    }
}

package com.backend.stockmaster.issue.application.service;

import com.backend.stockmaster.core.exception.BusinessException;
import com.backend.stockmaster.core.exception.ResourceNotFoundException;
import com.backend.stockmaster.issue.application.dto.*;
import com.backend.stockmaster.issue.domain.*;
import com.backend.stockmaster.issue.repository.GoodsIssueRepository;
import com.backend.stockmaster.product.repository.ProductRepository;
import com.backend.stockmaster.stock.application.service.StockApplicationService;
import com.backend.stockmaster.stock.domain.MovementType;
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
public class GoodsIssueService {

    private final GoodsIssueRepository goodsIssueRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockApplicationService stockService;

    public Page<IssueDTO> findAll(Pageable pageable) {
        return goodsIssueRepository.findAll(pageable).map(this::toDTO);
    }

    public IssueDTO findById(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional
    public IssueDTO create(IssueCreateDTO dto) {
        GoodsIssue issue = GoodsIssue.builder()
                .numero(generateNumero())
                .entrepotId(dto.getEntrepotId())
                .motif(dto.getMotif())
                .clientNom(dto.getClientNom())
                .clientReference(dto.getClientReference())
                .dateSortie(dto.getDateSortie())
                .note(dto.getNote())
                .statut(IssueStatus.BROUILLON)
                .creePar(currentUser())
                .build();
        if (dto.getLignes() != null) {
            dto.getLignes().forEach(l -> issue.getLignes().add(
                    GoodsIssueLine.builder()
                            .bonSortie(issue)
                            .produitId(l.getProduitId())
                            .quantiteDemandee(l.getQuantiteDemandee())
                            .build()));
        }
        return toDTO(goodsIssueRepository.save(issue));
    }

    @Transactional
    public IssueDTO submit(Long id) {
        GoodsIssue issue = findOrThrow(id);
        if (issue.getStatut() != IssueStatus.BROUILLON)
            throw new BusinessException("Seul un brouillon peut être soumis.");
        issue.setStatut(IssueStatus.EN_ATTENTE_VALIDATION);
        return toDTO(goodsIssueRepository.save(issue));
    }

    @Transactional
    public IssueDTO validate(Long id) {
        GoodsIssue issue = findOrThrow(id);
        if (issue.getStatut() != IssueStatus.EN_ATTENTE_VALIDATION)
            throw new BusinessException("Seul un bon en attente peut être validé.");
        issue.getLignes().forEach(l ->
                stockService.removeStock(l.getProduitId(), issue.getEntrepotId(),
                        l.getQuantiteDemandee(), MovementType.SORTIE,
                        issue.getNumero(), null, "Bon de sortie " + issue.getNumero()));
        issue.getLignes().forEach(l -> l.setQuantiteSortie(l.getQuantiteDemandee()));
        issue.setStatut(IssueStatus.VALIDE);
        issue.setValideePar(currentUser());
        return toDTO(goodsIssueRepository.save(issue));
    }

    @Transactional
    public IssueDTO cancel(Long id) {
        GoodsIssue issue = findOrThrow(id);
        if (issue.getStatut() == IssueStatus.VALIDE)
            throw new BusinessException("Un bon validé ne peut pas être annulé.");
        issue.setStatut(IssueStatus.ANNULE);
        return toDTO(goodsIssueRepository.save(issue));
    }

    // Legacy aliases kept for controller compatibility
    public IssueDTO createIssue(IssueCreateDTO dto, String username) { return create(dto); }
    public void validateIssue(Long id, String username) { validate(id); }
    public void cancelIssue(Long id) { cancel(id); }
    public void rejectIssue(Long id) { cancel(id); }

    private GoodsIssue findOrThrow(Long id) {
        return goodsIssueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bon de sortie introuvable : " + id));
    }

    private String generateNumero() {
        int year = LocalDate.now().getYear();
        long count = goodsIssueRepository.count() + 1;
        return String.format("BS-%d-%04d", year, count);
    }

    private String currentUser() {
        try { return SecurityContextHolder.getContext().getAuthentication().getName(); }
        catch (Exception e) { return "system"; }
    }

    private IssueDTO toDTO(GoodsIssue i) {
        IssueDTO dto = IssueDTO.builder()
                .id(i.getId()).numero(i.getNumero())
                .entrepotId(i.getEntrepotId())
                .motif(i.getMotif()).clientNom(i.getClientNom())
                .clientReference(i.getClientReference())
                .statut(i.getStatut()).dateSortie(i.getDateSortie())
                .note(i.getNote()).creePar(i.getCreePar())
                .valideePar(i.getValideePar()).build();
        warehouseRepository.findById(i.getEntrepotId())
                .ifPresent(w -> dto.setEntrepotNom(w.getNom()));
        dto.setLignes(i.getLignes().stream().map(l -> {
            IssueLineDTO ld = IssueLineDTO.builder()
                    .id(l.getId()).produitId(l.getProduitId())
                    .quantiteDemandee(l.getQuantiteDemandee())
                    .quantiteSortie(l.getQuantiteSortie()).build();
            productRepository.findById(l.getProduitId())
                    .ifPresent(p -> ld.setProduitNom(p.getNom()));
            return ld;
        }).collect(Collectors.toList()));
        return dto;
    }
}

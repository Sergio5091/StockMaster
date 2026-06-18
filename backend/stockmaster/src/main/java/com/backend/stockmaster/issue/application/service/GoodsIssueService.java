package com.backend.stockmaster.issue.application.service;

import com.backend.stockmaster.issue.application.dto.*;
import com.backend.stockmaster.issue.repository.GoodsIssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoodsIssueService {

    private final GoodsIssueRepository goodsIssueRepository;

    public IssueDTO create(IssueCreateDTO dto) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }

    public IssueDTO submit(Long id) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }

    public IssueDTO validate(Long id) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }

    public IssueDTO cancel(Long id) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }

    public IssueDTO createIssue(IssueCreateDTO dto, String username) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }

    public void validateIssue(Long id, String username) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }

    public void cancelIssue(Long id) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }

    public void rejectIssue(Long id) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }

    public Page<IssueDTO> findAll(Pageable pageable) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }

    public IssueDTO findById(Long id) {
        // TODO: Implémentation à faire
        throw new UnsupportedOperationException("À implémenter");
    }
}
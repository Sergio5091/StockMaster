package com.backend.stockmaster.issue.repository;

import com.backend.stockmaster.issue.domain.GoodsIssue;
import com.backend.stockmaster.issue.domain.IssueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsIssueRepository extends JpaRepository<GoodsIssue, Long> {
    Page<GoodsIssue> findByEntrepotId(Long entrepotId, Pageable pageable);
    Page<GoodsIssue> findByStatut(IssueStatus statut, Pageable pageable);
    long count();
}

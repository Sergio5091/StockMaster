package com.backend.stockmaster.alert.repository;

import com.backend.stockmaster.alert.domain.Alert;
import com.backend.stockmaster.alert.domain.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    Page<Alert> findByTraitee(boolean traitee, Pageable pageable);
    Page<Alert> findByType(AlertType type, Pageable pageable);
    long countByTraiteeFalse();
    List<Alert> findTop10ByTraiteeFalseOrderByCreatedAtDesc();
}

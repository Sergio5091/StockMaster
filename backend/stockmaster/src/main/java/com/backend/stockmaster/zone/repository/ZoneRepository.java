package com.backend.stockmaster.zone.repository;

import com.backend.stockmaster.zone.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    List<Zone> findByEntrepotIdAndActifTrue(Long entrepotId);
    List<Zone> findByEntrepotId(Long entrepotId);
    boolean existsByCodeAndEntrepotId(String code, Long entrepotId);
    long countByEntrepotIdAndActifTrue(Long entrepotId);
}

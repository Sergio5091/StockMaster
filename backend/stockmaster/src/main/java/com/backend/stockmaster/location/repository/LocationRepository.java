package com.backend.stockmaster.location.repository;

import com.backend.stockmaster.location.domain.Location;
import com.backend.stockmaster.location.domain.LocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByZoneIdAndActifTrue(Long zoneId);
    long countByZoneIdAndStatut(Long zoneId, LocationStatus statut);
    long countByZoneId(Long zoneId);
}

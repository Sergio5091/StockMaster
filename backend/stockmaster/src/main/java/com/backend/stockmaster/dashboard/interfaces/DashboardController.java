package com.backend.stockmaster.dashboard.interfaces;

import com.backend.stockmaster.dashboard.application.dto.DashboardKPIDTO;
import com.backend.stockmaster.dashboard.application.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/kpis")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE_ENTREPOT', 'AUDITEUR')")
    public ResponseEntity<DashboardKPIDTO> getKPIs() {
        return ResponseEntity.ok(dashboardService.getDashboardKPIs());
    }
}

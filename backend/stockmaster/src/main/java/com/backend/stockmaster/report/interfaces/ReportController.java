package com.backend.stockmaster.report.interfaces;

import com.backend.stockmaster.report.application.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // ============ INVENTORY REPORTS ============
    @GetMapping("/inventory/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE_ENTREPOT', 'AUDITEUR')")
    public ResponseEntity<byte[]> getInventoryReportPDF() throws Exception {
        byte[] pdf = reportService.generateInventoryReportPDF();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport_inventaire.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/inventory/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE_ENTREPOT', 'AUDITEUR')")
    public ResponseEntity<byte[]> getInventoryReportExcel() throws Exception {
        byte[] excel = reportService.generateInventoryReportExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport_inventaire.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @GetMapping("/inventory/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE_ENTREPOT', 'AUDITEUR')")
    public ResponseEntity<byte[]> getInventoryReportCSV() throws Exception {
        byte[] csv = reportService.generateInventoryReportCSV();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport_inventaire.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    // ============ MOVEMENTS REPORTS ============
    @GetMapping("/movements/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE_ENTREPOT', 'AUDITEUR')")
    public ResponseEntity<byte[]> getMovementsReportPDF() throws Exception {
        byte[] pdf = reportService.generateMovementsReportPDF();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport_mouvements.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/movements/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE_ENTREPOT', 'AUDITEUR')")
    public ResponseEntity<byte[]> getMovementsReportExcel() throws Exception {
        byte[] excel = reportService.generateMovementsReportExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport_mouvements.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @GetMapping("/movements/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE_ENTREPOT', 'AUDITEUR')")
    public ResponseEntity<byte[]> getMovementsReportCSV() throws Exception {
        byte[] csv = reportService.generateMovementsReportCSV();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport_mouvements.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    // ============ RECEIPTS REPORTS ============
    @GetMapping("/receipts/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE_ENTREPOT', 'AUDITEUR')")
    public ResponseEntity<byte[]> getReceiptsReportPDF() throws Exception {
        byte[] pdf = reportService.generateReceiptsReportPDF();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport_receptions.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/receipts/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE_ENTREPOT', 'AUDITEUR')")
    public ResponseEntity<byte[]> getReceiptsReportExcel() throws Exception {
        byte[] excel = reportService.generateReceiptsReportExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport_receptions.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @GetMapping("/receipts/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE_ENTREPOT', 'AUDITEUR')")
    public ResponseEntity<byte[]> getReceiptsReportCSV() throws Exception {
        byte[] csv = reportService.generateReceiptsReportCSV();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport_receptions.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}

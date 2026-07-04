package com.backend.stockmaster.report.application.service;

import com.backend.stockmaster.inventory.repository.InventoryRepository;
import com.backend.stockmaster.receipt.repository.GoodsReceiptRepository;
import com.backend.stockmaster.issue.repository.GoodsIssueRepository;
import com.backend.stockmaster.stock.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.LocalDate;

/**
 * Service de génération de rapports.
 * PDF/Excel/CSV désactivés temporairement (dépendances iText/POI/commons-csv
 * non disponibles en mode offline). Les endpoints retournent du CSV simple.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final InventoryRepository inventoryRepository;
    private final GoodsReceiptRepository goodsReceiptRepository;
    private final GoodsIssueRepository goodsIssueRepository;
    private final StockMovementRepository stockMovementRepository;

    // ===== INVENTAIRES =====

    public byte[] generateInventoryReportCSV() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(stream));
        writer.println("ID,Numéro,Statut,Lignes,Date");
        inventoryRepository.findAll().forEach(inv -> {
            int lignes = inv.getLignes() != null ? inv.getLignes().size() : 0;
            writer.printf("%s,%s,%s,%d,%s%n",
                    inv.getId(), inv.getNumero(), inv.getStatut(),
                    lignes, inv.getCreatedAt() != null ? inv.getCreatedAt().toLocalDate() : "");
        });
        writer.flush();
        return stream.toByteArray();
    }

    public byte[] generateInventoryReportPDF() throws Exception {
        return generateInventoryReportCSV(); // stub - retourne CSV
    }

    public byte[] generateInventoryReportExcel() throws Exception {
        return generateInventoryReportCSV(); // stub - retourne CSV
    }

    // ===== MOUVEMENTS =====

    public byte[] generateMovementsReportCSV() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(stream));
        writer.println("ID,ProduitId,Type,Quantite,Avant,Après,Document,Date");
        stockMovementRepository.findAll().forEach(m -> {
            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                    m.getId(), m.getProduitId(), m.getType(), m.getQuantite(),
                    m.getQuantiteAvant(), m.getQuantiteApres(),
                    m.getReferenceDocument() != null ? m.getReferenceDocument() : "",
                    m.getCreatedAt() != null ? m.getCreatedAt().toLocalDate() : "");
        });
        writer.flush();
        return stream.toByteArray();
    }

    public byte[] generateMovementsReportPDF() throws Exception {
        return generateMovementsReportCSV();
    }

    public byte[] generateMovementsReportExcel() throws Exception {
        return generateMovementsReportCSV();
    }

    // ===== RÉCEPTIONS =====

    public byte[] generateReceiptsReportCSV() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(stream));
        writer.println("Numéro,FournisseurId,Statut,Lignes,DateRéception,CréePar");
        goodsReceiptRepository.findAll().forEach(r -> {
            int lignes = r.getLignes() != null ? r.getLignes().size() : 0;
            writer.printf("%s,%s,%s,%d,%s,%s%n",
                    r.getNumero(), r.getFournisseurId(), r.getStatut(),
                    lignes,
                    r.getDateReception() != null ? r.getDateReception() : "",
                    r.getCreePar() != null ? r.getCreePar() : "");
        });
        writer.flush();
        return stream.toByteArray();
    }

    public byte[] generateReceiptsReportPDF() throws Exception {
        return generateReceiptsReportCSV();
    }

    public byte[] generateReceiptsReportExcel() throws Exception {
        return generateReceiptsReportCSV();
    }

    // ===== SORTIES =====

    public byte[] generateIssuesReportCSV() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(stream));
        writer.println("Numéro,EntrepôtId,Motif,Statut,DateSortie,CréePar");
        goodsIssueRepository.findAll().forEach(i -> {
            writer.printf("%s,%s,%s,%s,%s,%s%n",
                    i.getNumero(), i.getEntrepotId(), i.getMotif(), i.getStatut(),
                    i.getDateSortie() != null ? i.getDateSortie() : "",
                    i.getCreePar() != null ? i.getCreePar() : "");
        });
        writer.flush();
        return stream.toByteArray();
    }
}

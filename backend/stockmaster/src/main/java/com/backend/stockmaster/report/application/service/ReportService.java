package com.backend.stockmaster.report.application.service;

import com.backend.stockmaster.inventory.repository.InventoryRepository;
import com.backend.stockmaster.inventory.domain.Inventory;
import com.backend.stockmaster.receipt.repository.GoodsReceiptRepository;
import com.backend.stockmaster.issue.repository.GoodsIssueRepository;
import com.backend.stockmaster.stock.repository.StockMovementRepository;
import com.backend.stockmaster.stock.domain.StockMovement;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final InventoryRepository inventoryRepository;
    private final GoodsReceiptRepository goodsReceiptRepository;
    private final GoodsIssueRepository goodsIssueRepository;
    private final StockMovementRepository stockMovementRepository;

    // ============ INVENTORY REPORTS ============
    public byte[] generateInventoryReportPDF() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, stream);
        document.open();

        document.add(new Paragraph("RAPPORT D'INVENTAIRE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
        document.add(new Paragraph("Généré le: " + LocalDate.now(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        
        addTableHeader(table, new String[]{"ID", "Entrepôt", "Statut", "Prod. Comptés", "Écarts Détectés", "Date"});

        inventoryRepository.findAll().forEach(inv -> {
            table.addCell(inv.getId().toString());
            table.addCell(inv.getNumero());
            table.addCell(inv.getStatut().toString());
            table.addCell(String.valueOf(inv.getLignes() != null ? inv.getLignes().size() : 0));
            long discrepancies = inv.getLignes() != null ? inv.getLignes().stream()
                    .filter(l -> l.getEcart() != null && l.getEcart() != 0).count() : 0;
            table.addCell(String.valueOf(discrepancies));
            table.addCell(inv.getCreatedAt() != null ? inv.getCreatedAt().toString() : "");
        });

        document.add(table);
        document.close();
        
        return stream.toByteArray();
    }

    public byte[] generateInventoryReportExcel() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventaires");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Entrepôt");
        header.createCell(2).setCellValue("Statut");
        header.createCell(3).setCellValue("Produits Comptés");
        header.createCell(4).setCellValue("Écarts");
        header.createCell(5).setCellValue("Date");

        int rowNum = 1;
        for (Inventory inv : inventoryRepository.findAll()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(inv.getId());
            row.createCell(1).setCellValue(inv.getNumero());
            row.createCell(2).setCellValue(inv.getStatut().toString());
            row.createCell(3).setCellValue(inv.getLignes() != null ? inv.getLignes().size() : 0);
            long discrepancies = inv.getLignes() != null ? inv.getLignes().stream()
                    .filter(l -> l.getEcart() != null && l.getEcart() != 0).count() : 0;
            row.createCell(4).setCellValue(discrepancies);
            row.createCell(5).setCellValue(inv.getCreatedAt() != null ? inv.getCreatedAt().toString() : "");
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        workbook.write(stream);
        workbook.close();
        
        return stream.toByteArray();
    }

    public byte[] generateInventoryReportCSV() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                "ID", "Entrepôt", "Statut", "Produits Comptés", "Écarts", "Date"
        ));

        for (Inventory inv : inventoryRepository.findAll()) {
            long discrepancies = inv.getLignes() != null ? inv.getLignes().stream()
                    .filter(l -> l.getEcart() != null && l.getEcart() != 0).count() : 0;
            
            printer.printRecord(
                    inv.getId(),
                    inv.getNumero(),
                    inv.getStatut(),
                    inv.getLignes() != null ? inv.getLignes().size() : 0,
                    discrepancies,
                    inv.getCreatedAt()
            );
        }

        printer.flush();
        return stream.toByteArray();
    }

    // ============ MOVEMENTS REPORTS ============
    public byte[] generateMovementsReportPDF() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, stream);
        document.open();

        document.add(new Paragraph("RAPPORT DES MOUVEMENTS DE STOCK", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
        document.add(new Paragraph("Généré le: " + LocalDate.now(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        
        addTableHeader(table, new String[]{"ID", "Produit", "Type", "Quantité", "Entrepôt", "Date", "Utilisateur"});

        stockMovementRepository.findAll().forEach(movement -> {
            table.addCell(movement.getId().toString());
            table.addCell(movement.getProduitId() != null ? movement.getProduitId().toString() : "");
            table.addCell(movement.getType() != null ? movement.getType().toString() : "");
            table.addCell(movement.getQuantite() != null ? movement.getQuantite().toString() : "");
            table.addCell("");
            table.addCell(movement.getCreatedAt() != null ? movement.getCreatedAt().toString() : "");
            table.addCell("");
        });

        document.add(table);
        document.close();
        
        return stream.toByteArray();
    }

    public byte[] generateMovementsReportExcel() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Mouvements");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Produit");
        header.createCell(2).setCellValue("Type");
        header.createCell(3).setCellValue("Quantité");
        header.createCell(4).setCellValue("Entrepôt");
        header.createCell(5).setCellValue("Date");
        header.createCell(6).setCellValue("Utilisateur");

        int rowNum = 1;
        for (StockMovement movement : stockMovementRepository.findAll()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(movement.getId());
            row.createCell(1).setCellValue(movement.getProduitId() != null ? movement.getProduitId().toString() : "");
            row.createCell(2).setCellValue(movement.getType() != null ? movement.getType().toString() : "");
            row.createCell(3).setCellValue(movement.getQuantite() != null ? movement.getQuantite() : 0);
            row.createCell(4).setCellValue("");
            row.createCell(5).setCellValue(movement.getCreatedAt() != null ? movement.getCreatedAt().toString() : "");
            row.createCell(6).setCellValue("");
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        workbook.write(stream);
        workbook.close();
        
        return stream.toByteArray();
    }

    public byte[] generateMovementsReportCSV() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                "ID", "Produit", "Type", "Quantité", "Entrepôt", "Date", "Utilisateur"
        ));

        for (StockMovement movement : stockMovementRepository.findAll()) {
            printer.printRecord(
                    movement.getId(),
                    movement.getProduitId(),
                    movement.getType(),
                    movement.getQuantite(),
                    "",
                    movement.getCreatedAt(),
                    ""
            );
        }

        printer.flush();
        return stream.toByteArray();
    }

    // ============ RECEIPTS REPORTS ============
    public byte[] generateReceiptsReportPDF() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, stream);
        document.open();

        document.add(new Paragraph("RAPPORT DES RÉCEPTIONS", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
        document.add(new Paragraph("Généré le: " + LocalDate.now(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        
        addTableHeader(table, new String[]{"Numéro", "Fournisseur", "Statut", "Produits", "Date Réception", "Créé Par"});

        goodsReceiptRepository.findAll().forEach(receipt -> {
            table.addCell(receipt.getNumero() != null ? receipt.getNumero() : "");
            table.addCell(receipt.getFournisseurId() != null ? receipt.getFournisseurId().toString() : "");
            table.addCell(receipt.getStatut().toString());
            table.addCell(receipt.getLignes() != null ? String.valueOf(receipt.getLignes().size()) : "0");
            table.addCell(receipt.getDateReception() != null ? receipt.getDateReception().toString() : "");
            table.addCell(receipt.getCreePar() != null ? receipt.getCreePar() : "");
        });

        document.add(table);
        document.close();
        
        return stream.toByteArray();
    }

    public byte[] generateReceiptsReportExcel() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Réceptions");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Numéro");
        header.createCell(1).setCellValue("Fournisseur ID");
        header.createCell(2).setCellValue("Statut");
        header.createCell(3).setCellValue("Produits");
        header.createCell(4).setCellValue("Date Réception");
        header.createCell(5).setCellValue("Créé Par");

        int rowNum = 1;
        for (var receipt : goodsReceiptRepository.findAll()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(receipt.getNumero() != null ? receipt.getNumero() : "");
            row.createCell(1).setCellValue(receipt.getFournisseurId() != null ? receipt.getFournisseurId() : 0);
            row.createCell(2).setCellValue(receipt.getStatut().toString());
            row.createCell(3).setCellValue(receipt.getLignes() != null ? receipt.getLignes().size() : 0);
            row.createCell(4).setCellValue(receipt.getDateReception() != null ? receipt.getDateReception().toString() : "");
            row.createCell(5).setCellValue(receipt.getCreePar() != null ? receipt.getCreePar() : "");
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        workbook.write(stream);
        workbook.close();
        
        return stream.toByteArray();
    }

    public byte[] generateReceiptsReportCSV() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                "Numéro", "Fournisseur ID", "Statut", "Produits", "Date Réception", "Créé Par"
        ));

        for (var receipt : goodsReceiptRepository.findAll()) {
            printer.printRecord(
                    receipt.getNumero(),
                    receipt.getFournisseurId(),
                    receipt.getStatut(),
                    receipt.getLignes() != null ? receipt.getLignes().size() : 0,
                    receipt.getDateReception(),
                    receipt.getCreePar()
            );
        }

        printer.flush();
        return stream.toByteArray();
    }

    private void addTableHeader(PdfPTable table, String[] headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Paragraph(header));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }
}

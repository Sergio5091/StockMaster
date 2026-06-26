package com.backend.stockmaster.dashboard.application.service;

import com.backend.stockmaster.dashboard.application.dto.*;
import com.backend.stockmaster.warehouse.repository.WarehouseRepository;
import com.backend.stockmaster.product.repository.ProductRepository;
import com.backend.stockmaster.stock.repository.StockRepository;
import com.backend.stockmaster.stock.repository.StockMovementRepository;
import com.backend.stockmaster.receipt.repository.GoodsReceiptRepository;
import com.backend.stockmaster.issue.repository.GoodsIssueRepository;
import com.backend.stockmaster.transfer.repository.TransferRepository;
import com.backend.stockmaster.inventory.repository.InventoryRepository;
import com.backend.stockmaster.alert.repository.AlertRepository;
import com.backend.stockmaster.category.repository.CategoryRepository;
import com.backend.stockmaster.alert.domain.AlertType;
import com.backend.stockmaster.inventory.domain.InventoryStatus;
import com.backend.stockmaster.transfer.domain.TransferStatus;
import com.backend.stockmaster.receipt.domain.ReceiptStatus;
import com.backend.stockmaster.issue.domain.IssueStatus;
import com.backend.stockmaster.receipt.domain.GoodsReceipt;
import com.backend.stockmaster.issue.domain.GoodsIssue;
import com.backend.stockmaster.transfer.domain.Transfer;
import com.backend.stockmaster.inventory.domain.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final GoodsReceiptRepository goodsReceiptRepository;
    private final GoodsIssueRepository goodsIssueRepository;
    private final TransferRepository transferRepository;
    private final InventoryRepository inventoryRepository;
    private final AlertRepository alertRepository;
    private final CategoryRepository categoryRepository;

    public DashboardKPIDTO getDashboardKPIs() {
        return DashboardKPIDTO.builder()
                .totalWarehouses(calculateTotalWarehouses())
                .activeWarehouses(calculateActiveWarehouses())
                .totalProducts(calculateTotalProducts())
                .productsInStock(calculateProductsInStock())
                .criticalStockProducts(calculateCriticalStockProducts())
                .totalStockValue(calculateTotalStockValue())
                .totalMovementsThisMonth(calculateTotalMovementsThisMonth())
                .incomingMovementsThisMonth(calculateIncomingMovementsThisMonth())
                .outgoingMovementsThisMonth(calculateOutgoingMovementsThisMonth())
                .pendingInventories(calculatePendingInventories())
                .pendingTransfers(calculatePendingTransfers())
                .pendingReceipts(calculatePendingReceipts())
                .activeAlerts(calculateActiveAlerts())
                .stockMovementChart(getStockMovementChart())
                .warehouseCapacity(getWarehouseCapacity())
                .productByCategory(getProductByCategory())
                .monthlyRevenue(getMonthlyRevenue())
                .recentActivities(getRecentActivities())
                .build();
    }

    private Long calculateTotalWarehouses() {
        return warehouseRepository.count();
    }

    private Long calculateActiveWarehouses() {
        return warehouseRepository.findAll().stream()
                .filter(Warehouse -> Warehouse.isActif())
                .count();
    }

    private Long calculateTotalProducts() {
        return productRepository.count();
    }

    private Long calculateProductsInStock() {
        return stockRepository.findAll().stream()
                .filter(s -> s.getQuantiteDisponible() > 0)
                .count();
    }

    private Long calculateCriticalStockProducts() {
        return productRepository.findAll().stream()
                .filter(p -> {
                    var stock = stockRepository.findAll().stream()
                            .filter(s -> s.getProduitId().equals(p.getId()))
                            .findFirst();
                    return stock.isPresent() && 
                           p.getStockMinimum() != null &&
                           stock.get().getQuantiteDisponible() <= p.getStockMinimum();
                })
                .count();
    }

    private BigDecimal calculateTotalStockValue() {
        return productRepository.findAll().stream()
                .map(p -> {
                    var stock = stockRepository.findAll().stream()
                            .filter(s -> s.getProduitId().equals(p.getId()))
                            .findFirst();
                    if (stock.isPresent() && p.getPrixAchat() != null) {
                        return BigDecimal.valueOf(stock.get().getQuantiteDisponible())
                                .multiply(p.getPrixAchat());
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Long calculateTotalMovementsThisMonth() {
        LocalDateTime firstDay = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        return stockMovementRepository.findAll().stream()
                .filter(m -> m.getCreatedAt() != null && m.getCreatedAt().isAfter(firstDay))
                .count();
    }

    private Long calculateIncomingMovementsThisMonth() {
        LocalDateTime firstDay = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        return stockMovementRepository.findAll().stream()
                .filter(m -> m.getCreatedAt() != null && m.getCreatedAt().isAfter(firstDay)
                        && com.backend.stockmaster.stock.domain.MovementType.ENTREE == m.getType())
                .count();
    }

    private Long calculateOutgoingMovementsThisMonth() {
        LocalDateTime firstDay = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        return stockMovementRepository.findAll().stream()
                .filter(m -> m.getCreatedAt() != null && m.getCreatedAt().isAfter(firstDay)
                        && com.backend.stockmaster.stock.domain.MovementType.SORTIE == m.getType())
                .count();
    }

    private Long calculatePendingInventories() {
        return inventoryRepository.findAll().stream()
                .filter(i -> i.getStatut() != InventoryStatus.TERMINE)
                .count();
    }

    private Long calculatePendingTransfers() {
        return transferRepository.findAll().stream()
                .filter(t -> t.getStatut() != TransferStatus.RECU)
                .count();
    }

    private Long calculatePendingReceipts() {
        return goodsReceiptRepository.findAll().stream()
                .filter(r -> r.getStatut() != ReceiptStatus.VALIDE)
                .count();
    }

    private Long calculateActiveAlerts() {
        return alertRepository.countByTraiteeFalse();
    }

    private List<StockMovementChartDTO> getStockMovementChart() {
        List<StockMovementChartDTO> result = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            YearMonth month = YearMonth.now().minusMonths(i);
            LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);

            long incoming = stockMovementRepository.findAll().stream()
                    .filter(m -> m.getCreatedAt() != null 
                            && m.getCreatedAt().isAfter(startOfMonth)
                            && m.getCreatedAt().isBefore(endOfMonth)
                            && "ENTREE".equals(m.getType()))
                    .mapToLong(m -> m.getQuantite() != null ? m.getQuantite() : 0)
                    .sum();

            long outgoing = stockMovementRepository.findAll().stream()
                    .filter(m -> m.getCreatedAt() != null 
                            && m.getCreatedAt().isAfter(startOfMonth)
                            && m.getCreatedAt().isBefore(endOfMonth)
                            && "SORTIE".equals(m.getType()))
                    .mapToLong(m -> m.getQuantite() != null ? m.getQuantite() : 0)
                    .sum();

            result.add(StockMovementChartDTO.builder()
                    .month(month.toString())
                    .incoming(incoming)
                    .outgoing(outgoing)
                    .netChange(incoming - outgoing)
                    .build());
        }
        return result;
    }

    private List<WarehouseCapacityDTO> getWarehouseCapacity() {
        return warehouseRepository.findAll().stream()
                .filter(w -> w.isActif())
                .map(w -> {
                    BigDecimal total = w.getCapaciteTotale() != null ? BigDecimal.valueOf(w.getCapaciteTotale()) : BigDecimal.ONE;
                    BigDecimal utilized = BigDecimal.ZERO;
                    double percent = total.doubleValue() > 0 ? (utilized.doubleValue() / total.doubleValue()) * 100 : 0;
                    
                    return WarehouseCapacityDTO.builder()
                            .warehouseId(w.getId())
                            .warehouseName(w.getNom())
                            .usedCapacity(utilized)
                            .totalCapacity(total)
                            .utilizationPercent(Math.round(percent * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<ProductCategoryChartDTO> getProductByCategory() {
        return categoryRepository.findAll().stream()
                .map(c -> {
                    long productCount = productRepository.findAll().stream()
                            .filter(p -> c.getId().equals(p.getCategorieId()))
                            .count();
                    long totalQuantity = stockRepository.findAll().stream()
                            .filter(s -> productRepository.findById(s.getProduitId())
                                    .map(p -> c.getId().equals(p.getCategorieId()))
                                    .orElse(false))
                            .mapToLong(s -> s.getQuantiteDisponible() != null ? s.getQuantiteDisponible() : 0)
                            .sum();
                    
                    return ProductCategoryChartDTO.builder()
                            .categoryName(c.getNom())
                            .productCount(productCount)
                            .totalQuantity(totalQuantity)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<MonthlyRevenueDTO> getMonthlyRevenue() {
        Map<YearMonth, BigDecimal> revenueByMonth = new TreeMap<>();
        
        goodsIssueRepository.findAll().stream()
                .filter(i -> i.getCreatedAt() != null && i.getStatut() == IssueStatus.VALIDE)
                .forEach(issue -> {
                    YearMonth month = YearMonth.from(issue.getCreatedAt());
                    BigDecimal revenue = revenueByMonth.getOrDefault(month, BigDecimal.ZERO);
                    
                    BigDecimal issueRevenue = issue.getLignes().stream()
                            .map(line -> BigDecimal.valueOf(line.getQuantiteSortie() != null ? line.getQuantiteSortie() : 0))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    revenueByMonth.put(month, revenue.add(issueRevenue));
                });

        return revenueByMonth.entrySet().stream()
                .map(e -> MonthlyRevenueDTO.builder()
                        .month(e.getKey().toString())
                        .revenue(e.getValue())
                        .transactionCount(goodsIssueRepository.findAll().stream()
                                .filter(i -> i.getCreatedAt() != null 
                                        && YearMonth.from(i.getCreatedAt()).equals(e.getKey())
                                        && i.getStatut() == IssueStatus.VALIDE)
                                .count())
                        .build())
                .collect(Collectors.toList());
    }

    private List<RecentActivityDTO> getRecentActivities() {
        List<RecentActivityDTO> activities = new ArrayList<>();

        List<GoodsReceipt> recentReceipts = goodsReceiptRepository.findAll();
        recentReceipts.stream()
                .sorted(Comparator.comparing(GoodsReceipt::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .forEach(r -> activities.add(RecentActivityDTO.builder()
                        .id(r.getId())
                        .type("RECEIPT")
                        .description("Réception: " + r.getNumero())
                        .status(r.getStatut().toString())
                        .timestamp(r.getCreatedAt())
                        .username(r.getCreatedBy())
                        .build()));

        List<GoodsIssue> recentIssues = goodsIssueRepository.findAll();
        recentIssues.stream()
                .sorted(Comparator.comparing(GoodsIssue::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .forEach(i -> activities.add(RecentActivityDTO.builder()
                        .id(i.getId())
                        .type("ISSUE")
                        .description("Sortie: " + i.getNumero())
                        .status(i.getStatut().toString())
                        .timestamp(i.getCreatedAt())
                        .username(i.getCreatedBy())
                        .build()));

        List<Transfer> recentTransfers = transferRepository.findAll();
        recentTransfers.stream()
                .sorted(Comparator.comparing(Transfer::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .forEach(t -> activities.add(RecentActivityDTO.builder()
                        .id(t.getId())
                        .type("TRANSFER")
                        .description("Transfert: " + t.getNumero())
                        .status(t.getStatut().toString())
                        .timestamp(t.getCreatedAt())
                        .username(t.getCreePar())
                        .build()));

        List<Inventory> recentInventories = inventoryRepository.findAll();
        recentInventories.stream()
                .sorted(Comparator.comparing(Inventory::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .forEach(inv -> activities.add(RecentActivityDTO.builder()
                        .id(inv.getId())
                        .type("INVENTORY")
                        .description("Inventaire: " + inv.getNumero())
                        .status(inv.getStatut().toString())
                        .timestamp(inv.getCreatedAt())
                        .username(inv.getCreePar())
                        .build()));

        return activities.stream()
                .sorted(Comparator.comparing(RecentActivityDTO::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .collect(Collectors.toList());
    }
}

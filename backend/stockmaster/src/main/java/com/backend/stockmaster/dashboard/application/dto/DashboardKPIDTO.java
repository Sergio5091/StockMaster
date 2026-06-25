package com.backend.stockmaster.dashboard.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardKPIDTO {
    
    // Warehouse stats
    private Long totalWarehouses;
    private Long activeWarehouses;
    
    // Stock stats
    private Long totalProducts;
    private Long productsInStock;
    private Long criticalStockProducts;
    private BigDecimal totalStockValue;
    
    // Movement stats
    private Long totalMovementsThisMonth;
    private Long incomingMovementsThisMonth;
    private Long outgoingMovementsThisMonth;
    
    // Inventory & Transfers
    private Long pendingInventories;
    private Long pendingTransfers;
    private Long pendingReceipts;
    
    // Alerts
    private Long activeAlerts;
    
    // Charts data
    private List<StockMovementChartDTO> stockMovementChart;
    private List<WarehouseCapacityDTO> warehouseCapacity;
    private List<ProductCategoryChartDTO> productByCategory;
    private List<MonthlyRevenueDTO> monthlyRevenue;
    
    // Recent activities
    private List<RecentActivityDTO> recentActivities;
}

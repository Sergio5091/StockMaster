package com.backend.stockmaster.dashboard.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseCapacityDTO {
    private Long warehouseId;
    private String warehouseName;
    private BigDecimal usedCapacity;
    private BigDecimal totalCapacity;
    private Double utilizationPercent;
}

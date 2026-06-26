package com.backend.stockmaster.dashboard.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryChartDTO {
    private String categoryName;
    private Long productCount;
    private Long totalQuantity;
}

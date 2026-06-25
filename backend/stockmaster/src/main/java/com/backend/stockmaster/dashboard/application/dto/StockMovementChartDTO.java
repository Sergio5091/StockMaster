package com.backend.stockmaster.dashboard.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementChartDTO {
    private String month;
    private Long incoming;
    private Long outgoing;
    private Long netChange;
}

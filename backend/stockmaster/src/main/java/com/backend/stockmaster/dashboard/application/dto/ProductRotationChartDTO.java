package com.backend.stockmaster.dashboard.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRotationChartDTO {
    private String month;
    private Long productsMoved;
    private Long entries;
    private Long exits;
}

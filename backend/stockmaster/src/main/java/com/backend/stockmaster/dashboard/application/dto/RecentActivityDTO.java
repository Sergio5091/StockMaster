package com.backend.stockmaster.dashboard.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDTO {
    private Long id;
    private String type; // RECEIPT, ISSUE, TRANSFER, INVENTORY
    private String description;
    private String status;
    private LocalDateTime timestamp;
    private String username;
}

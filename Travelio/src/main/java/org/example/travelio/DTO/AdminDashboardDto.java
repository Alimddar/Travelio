package org.example.travelio.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AdminDashboardDto {
    private long totalBookings;
    private double totalRevenue;
    private Map<String, Long> statusCounts;
}


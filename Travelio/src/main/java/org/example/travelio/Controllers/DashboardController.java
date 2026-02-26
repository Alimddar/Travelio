package org.example.travelio.Controllers;

import org.example.travelio.DTO.ServiceUsageDTO;
import org.example.travelio.DTO.UserGrowthResponse;
import org.example.travelio.Services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/user-growth")
    public List<UserGrowthResponse> getUserGrowth() {
        return dashboardService.getUserGrowthTrend();
    }

    @GetMapping("/services-usage")
    public List<ServiceUsageDTO> getServicesUsage(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return dashboardService.getServiceUsage(startDate, endDate);
    }

}

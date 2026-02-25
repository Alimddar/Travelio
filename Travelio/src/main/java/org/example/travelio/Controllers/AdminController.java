package org.example.travelio.Controllers;

import jakarta.validation.Valid;
import org.example.travelio.DTO.Request.SystemParameterRequest;
import org.example.travelio.DTO.Response.*;
import org.example.travelio.Services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {


    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard/stats")
    public DashboardStatsResponse getDashboardStats() {
        return adminService.getDashboardStats();
    }

    @GetMapping("/dashboard/user-preferences")
    public UserPreferencesResponse getUserPreferences() {
        return adminService.getUserPreferences();
    }

    @GetMapping("/dashboard/onboarding-funnel")
    public OnboardingFunnelResponse getOnboardingFunnel() {
        return adminService.getOnboardingFunnel();
    }

    @GetMapping("/dashboard/interests-stats")
    public InterestsStatsResponse getInterestsStats() {
        return adminService.getInterestsStats();
    }

    @GetMapping("/dashboard/traveler-types")
    public List<TravelerTypesResponse> getTravelerTypes() {
        return adminService.getTravelerTypes();
    }

    @GetMapping("/dashboard/parameters")
    public SystemParameterResponse getSettings() {
        return adminService.getSystemParameters();
    }

    @PutMapping("/dashboard/parameters")
    public ResponseEntity<?> updateSettings(@RequestBody @Valid SystemParameterRequest dto) {
        adminService.updateSystemParameters(dto);
        return ResponseEntity.ok().build();
    }

    // Ticket 1
    @GetMapping("/trends/daily-30")
    public List<DailyTrendResponse> daily30() {
        return adminService.getDaylyTrend();
    }

    // Ticket 2
    @GetMapping("/trends/weekly-compare")
    public List<WeeklyCompareResponse> weeklyCompare() {
        return adminService.getWeeklyCompare();
    }

    // Ticket 3
    @GetMapping("/trends/hourly-activity")
    public List<HourlyActivityResponse> hourly() {
        return adminService.getHourlyActivityLast30Days();
    }

    // Ticket 4
    @GetMapping("/trends/monthly-12")
    public List<MonthlyTrendResponse> monthly12() {
        return adminService.getLast12MonthsTrend();
    }

    // Ticket 5
    @GetMapping("/trends/peak-hour")
    public PeakHourResponse peakHour() {
        return adminService.getPeakHourLast30Days();
    }


}

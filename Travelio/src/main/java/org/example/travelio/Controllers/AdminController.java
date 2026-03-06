package org.example.travelio.Controllers;

import jakarta.validation.Valid;
import org.example.travelio.DTO.AdminDashboardDto;
import org.example.travelio.DTO.Request.SystemParameterRequest;
import org.example.travelio.DTO.Response.*;
import org.example.travelio.Entities.Journey;
import org.example.travelio.Enums.JourneyStatus;
import org.example.travelio.Repositories.JourneyRepository;
import org.example.travelio.Services.AdminService;
import org.example.travelio.Services.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final JourneyRepository journeyRepository;
    private final BookingService bookingService;

    public AdminController(AdminService adminService, JourneyRepository journeyRepository, BookingService bookingService) {
        this.adminService = adminService;
        this.journeyRepository = journeyRepository;
        this.bookingService = bookingService;
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminDashboardDto> getStats() {
        return ResponseEntity.ok(adminService.getLegacyDashboardStats());
    }


    @GetMapping("/hotels")
    public ResponseEntity<?> getHotels(@RequestParam(defaultValue = "Baku") String city) {
        return ResponseEntity.ok(bookingService.getHotelsForAdminView(city));
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<Journey>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (status != null && !status.isEmpty()) {
            try {
                JourneyStatus journeyStatus = JourneyStatus.valueOf(status.toUpperCase());
                return ResponseEntity.ok(journeyRepository.findByStatus(journeyStatus, pageable));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok(journeyRepository.findAll(pageable));
    }

    @GetMapping("/tourists")
    public List<Map<String, String>> getTourists() {
        return adminService.getTouristContacts();
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

    @GetMapping("/dashboard/active-passive-stats")
    public ActivePassiveStatsResponse getActivePassiveStats() {
        return adminService.getActivePassiveStats();
    }

    @GetMapping("/dashboard/parameters")
    public SystemParameterResponse getSettings() {
        return adminService.getSystemParameters();
    }

    @PutMapping("/dashboard/parameters")
    public ResponseEntity<?> updateSettings(@RequestBody @Valid SystemParameterRequest dto) {
        adminService.updateSystemParameters(dto);
        return ResponseEntity.ok(Collections.singletonMap("message", "Parametrlər uğurla yeniləndi"));
    }

    @GetMapping("/trends/daily-30")
    public List<DailyTrendResponse> daily30() {
        return adminService.getDaylyTrend();
    }


    @GetMapping("/trends/weekly-compare")
    public List<WeeklyCompareResponse> weeklyCompare() {
        return adminService.getWeeklyCompare();
    }


    @GetMapping("/trends/hourly-activity")
    public List<HourlyActivityResponse> hourly() {
        return adminService.getHourlyActivityLast30Days();
    }


    @GetMapping("/trends/monthly-12")
    public List<MonthlyTrendResponse> monthly12() {
        return adminService.getLast12MonthsTrend();
    }


    @GetMapping("/trends/peak-hour")
    public PeakHourResponse peakHour() {
        return adminService.getPeakHourLast30Days();
    }
}

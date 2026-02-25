package org.example.travelio.Controllers;

import org.example.travelio.DTO.Response.DashboardStatsResponse;
import org.example.travelio.DTO.Response.InterestsStatsResponse;
import org.example.travelio.DTO.Response.OnboardingFunnelResponse;
import org.example.travelio.DTO.Response.TravelerTypesResponse;
import org.example.travelio.DTO.Response.UserPreferencesResponse;
import org.example.travelio.Services.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

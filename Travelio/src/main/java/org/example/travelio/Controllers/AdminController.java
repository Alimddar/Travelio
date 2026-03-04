package org.example.travelio.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.travelio.DTO.AdminDashboardDto;
import org.example.travelio.Entities.Journey;
import org.example.travelio.Enums.JourneyStatus;
import org.example.travelio.Repositories.JourneyRepository;
import org.example.travelio.Repositories.UserAdminRepository;
import org.example.travelio.Services.AdminService;
import org.example.travelio.Services.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final JourneyRepository journeyRepository;
    private final UserAdminRepository userAdminRepository;
    private final BookingService bookingService;

    @GetMapping("/stats")
    public ResponseEntity<AdminDashboardDto> getStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
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
}


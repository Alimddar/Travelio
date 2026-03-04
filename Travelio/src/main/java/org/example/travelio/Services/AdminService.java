package org.example.travelio.Services;

import lombok.RequiredArgsConstructor;
import org.example.travelio.DTO.AdminDashboardDto;
import org.example.travelio.Entities.Journey;
import org.example.travelio.Enums.JourneyStatus;
import org.example.travelio.Repositories.JourneyRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final JourneyRepository journeyRepository;

    @Cacheable(value = "adminStats", key = "'dashboard'")
    public AdminDashboardDto getDashboardStats() {
        var journeys = journeyRepository.findAll();

        double revenue = journeys.stream()
                .filter(j -> j.getStatus() == JourneyStatus.COMPLETED)
                .mapToDouble(j -> j.getTotalPrice() != null ? j.getTotalPrice() : 0.0)
                .sum();

        var statusMap = journeys.stream()
                .collect(Collectors.groupingBy(
                        j -> j.getRequestStatus() != null ? j.getRequestStatus().name() : "START",
                        Collectors.counting()
                ));

        long uniqueUsers = journeys.stream()
                .map(Journey::getUserEmail)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        return AdminDashboardDto.builder()
                .totalBookings(journeys.size())
                .totalRevenue(revenue)
                .statusCounts(statusMap)
                .build();
    }

    public List<Map<String, String>> getTouristContacts() {
        return journeyRepository.findAll().stream()
                .filter(j -> j.getUserEmail() != null)
                .map(j -> {
                    Map<String, String> contact = new HashMap<>();
                    contact.put("email", j.getUserEmail());
                    contact.put("whatsapp", j.getUserWhatsapp());
                    return contact;
                })
                .distinct()
                .collect(Collectors.toList());
    }
}
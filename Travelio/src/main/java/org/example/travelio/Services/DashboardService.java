package org.example.travelio.Services;

import org.example.travelio.DTO.ServiceUsageDTO;
import org.example.travelio.DTO.UserGrowthResponse;
import org.example.travelio.Entities.ServiceOrder;
import org.example.travelio.Entities.User;
import org.example.travelio.Repositories.ServiceOrderRepository;
import org.example.travelio.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    // Ticket 2
    public List<UserGrowthResponse> getUserGrowthTrend() {
        List<User> allUsers = userRepository.findAll();
        List<String> monthNames = List.of("Yan", "Fev", "Mar", "Apr", "May", "İyn");
        List<UserGrowthResponse> trend = new ArrayList<>();

        for (int i = 0; i < monthNames.size(); i++) {
            int currentMonth = i+1;

            long newUsersCount = allUsers.stream()
                    .filter(u -> u.getCreatedAt().getMonthValue() == currentMonth).count();
            long completedCount = allUsers.stream()
                    .filter(u->u.getCreatedAt().getMonthValue() == currentMonth && u.isOnboardingCompleted()).count();

            trend.add(new UserGrowthResponse(monthNames.get(i), newUsersCount, completedCount));
        }

        return trend;
    }


    public List<ServiceUsageDTO> getServiceUsage(LocalDateTime start, LocalDateTime end) {
        List<ServiceOrder> orders = serviceOrderRepository.findByOrderDateBetween(start, end);
        Map<String, ServiceUsageDTO> resultMap = new HashMap<>();

        for (ServiceOrder order : orders) {
            String dateStr = order.getOrderDate().toLocalDate().toString();
            String key = dateStr + ":" + order.getServiceName();

            if (resultMap.containsKey(key)) {
                ServiceUsageDTO dto = resultMap.get(key);
                dto.setUsageCount(dto.getUsageCount() + 1);
            } else {
                resultMap.put(key, new ServiceUsageDTO(dateStr, order.getServiceName(), 1L));
            }
        }
        return new ArrayList<>(resultMap.values());
    }
}

package org.example.travelio.Repositories;

import org.example.travelio.Entities.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    List<ServiceOrder> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
}

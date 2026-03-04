package org.example.travelio.Repositories;

import org.example.travelio.Entities.Journey;
import org.example.travelio.Enums.JourneyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface JourneyRepository extends JpaRepository<Journey, Long> {
    Page<Journey> findByStatus(JourneyStatus status, Pageable pageable);
}

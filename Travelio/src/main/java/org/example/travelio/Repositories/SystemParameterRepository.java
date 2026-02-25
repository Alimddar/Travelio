package org.example.travelio.Repositories;

import org.example.travelio.Entities.SystemParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemParameterRepository extends JpaRepository<SystemParameter, Long> {
    SystemParameter findFirstByOrderByIdDesc();
}
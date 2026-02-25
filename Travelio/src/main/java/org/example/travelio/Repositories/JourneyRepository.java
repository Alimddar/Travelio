package org.example.travelio.Repositories;

import org.example.travelio.Entities.Journey;
import org.example.travelio.Enums.BudgetType;
import org.example.travelio.Enums.JourneyStatus;
import org.example.travelio.Enums.TravelWith;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JourneyRepository extends CrudRepository<Journey, Long> {
    long countByStatus(JourneyStatus status);

    long countByBudgetType(BudgetType budgetType);

    long countByCurrentStepGreaterThanEqual(Long step);

    @Query("SELECT COALESCE(AVG(j.tripDays), 0) FROM Journey j")
    double findAverageTripDays();

    @Query("SELECT COUNT(j) FROM Journey j WHERE j.tripDays BETWEEN :min AND :max")
    long countByTripDaysBetween(long min, long max);

    @Query("SELECT COUNT(j) FROM Journey j WHERE j.tripDays > :min")
    long countByTripDaysGreaterThan(long min);

    @Query("SELECT i, COUNT(j) FROM Journey j JOIN j.interests i GROUP BY i ORDER BY COUNT(j) DESC")
    List<Object[]> countByInterests();

    long countByTravelWith(TravelWith travelWith);

    @Query("""
        select cast(j.createdAt as date) as d, count(j.id) as c
        from Journey j
        where j.createdAt >= :from and j.createdAt < :to
        group by cast(j.createdAt as date)
    """)
    List<Object[]> countRegistrationsByDay(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        select cast(j.createdAt as date) as d, count(j.id) as c
        from Journey j
        where j.createdAt >= :from and j.createdAt < :to and j.status = 'COMPLETED'
        group by cast(j.createdAt as date)
    """)
    List<Object[]> countOnboardedByDay(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        select hour(j.createdAt) as h, count(distinct j.id) as c
        from Journey j
        where j.createdAt >= :from and j.createdAt < :to
        group by hour(j.createdAt)
    """)
    List<Object[]> countDistinctUsersByHour(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        select cast(j.createdAt as date) as d, count(j.id) as c
        from Journey j
        where j.createdAt >= :from and j.createdAt < :to
        group by cast(j.createdAt as date)
    """)
    List<Object[]> countUsersByDay(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select j.createdAt from Journey j where j.createdAt >= :from and j.createdAt < :to")
    List<LocalDateTime> findCreatedAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}

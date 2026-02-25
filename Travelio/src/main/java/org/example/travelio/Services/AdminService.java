package org.example.travelio.Services;

import org.example.travelio.DTO.Response.DashboardStatsResponse;
import org.example.travelio.DTO.Response.InterestsStatsResponse;
import org.example.travelio.DTO.Response.OnboardingFunnelResponse;
import org.example.travelio.DTO.Response.TravelerTypesResponse;
import org.example.travelio.DTO.Response.UserPreferencesResponse;
import org.example.travelio.Enums.BudgetType;
import org.example.travelio.Enums.JourneyStatus;
import org.example.travelio.Enums.TravelWith;
import org.example.travelio.Repositories.JourneyRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AdminService {

    private final JourneyRepository journeyRepository;

    public AdminService(JourneyRepository journeyRepository) {
        this.journeyRepository = journeyRepository;
    }
    
    public DashboardStatsResponse getDashboardStats() {
        long totalUsers = journeyRepository.count();
        long onboardedUsers = journeyRepository.countByStatus(JourneyStatus.COMPLETED);
        double activeUsersPercentage = totalUsers > 0 ? (double) onboardedUsers / totalUsers * 100 : 0;
        double averageJourneyDuration = journeyRepository.findAverageTripDays();
        double overallConversionRate = totalUsers > 0 ? (double) onboardedUsers / totalUsers * 100 : 0;

        return new DashboardStatsResponse(totalUsers, onboardedUsers, activeUsersPercentage, averageJourneyDuration, overallConversionRate);
    }

    public UserPreferencesResponse getUserPreferences() {
        List<UserPreferencesResponse.PreferenceCount> budgets = List.of(
                new UserPreferencesResponse.PreferenceCount("Basic", journeyRepository.countByBudgetType(BudgetType.BASIC)),
                new UserPreferencesResponse.PreferenceCount("Standard", journeyRepository.countByBudgetType(BudgetType.STANDARD)),
                new UserPreferencesResponse.PreferenceCount("Luxury", journeyRepository.countByBudgetType(BudgetType.LUXURY))
        );

        List<UserPreferencesResponse.PreferenceCount> durations = List.of(
                new UserPreferencesResponse.PreferenceCount("1-3 days", journeyRepository.countByTripDaysBetween(1, 3)),
                new UserPreferencesResponse.PreferenceCount("4-7 days", journeyRepository.countByTripDaysBetween(4, 7)),
                new UserPreferencesResponse.PreferenceCount("8-14 days", journeyRepository.countByTripDaysBetween(8, 14)),
                new UserPreferencesResponse.PreferenceCount("14+ days", journeyRepository.countByTripDaysGreaterThan(14))
        );

        return new UserPreferencesResponse(budgets, durations);
    }

    public OnboardingFunnelResponse getOnboardingFunnel() {
        long step1Users = journeyRepository.countByCurrentStepGreaterThanEqual(1L);
        long step2Users = journeyRepository.countByCurrentStepGreaterThanEqual(2L);
        long step3Users = journeyRepository.countByCurrentStepGreaterThanEqual(3L);
        long completedUsers = journeyRepository.countByStatus(JourneyStatus.COMPLETED);

        double dropOffRate = step1Users > 0 ? (double) (step1Users - completedUsers) / step1Users * 100 : 0;
        
        return new OnboardingFunnelResponse(step1Users, step2Users, step3Users, completedUsers, dropOffRate);
    }

    public InterestsStatsResponse getInterestsStats() {
        long totalUsers = journeyRepository.count();
        List<Object[]> interestCounts = journeyRepository.countByInterests();

        List<InterestsStatsResponse.InterestStat> stats = interestCounts.stream()
                .map(row -> {
                    String name = (String) row[0];
                    long count = (Long) row[1];
                    double percentage = totalUsers > 0 ? (double) count / totalUsers * 100 : 0;
                    return new InterestsStatsResponse.InterestStat(name, count, percentage);
                })
                .toList();

        return new InterestsStatsResponse(stats);
    }

    public List<TravelerTypesResponse> getTravelerTypes() {
        long totalUsers = journeyRepository.count();

        return Arrays.stream(TravelWith.values())
                .map(type -> {
                    long count = journeyRepository.countByTravelWith(type);
                    double percentage = totalUsers > 0 ? (double) count / totalUsers * 100 : 0;
                    return new TravelerTypesResponse(
                            type.name().charAt(0) + type.name().substring(1).toLowerCase(),
                            count,
                            percentage
                    );
                })
                .toList();
    }
}

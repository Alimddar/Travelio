package org.example.travelio.Services;

import org.example.travelio.DTO.Request.SystemParameterRequest;
import org.example.travelio.DTO.Response.*;
import org.example.travelio.Entities.SystemParameter;
import org.example.travelio.Enums.BudgetType;
import org.example.travelio.Enums.JourneyStatus;
import org.example.travelio.Enums.TravelWith;
import org.example.travelio.Repositories.JourneyRepository;
import org.example.travelio.Repositories.SystemParameterRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.*;
import java.util.*;

@Service
public class AdminService {


    private final JourneyRepository journeyRepository;
    private final SystemParameterRepository systemParameterRepository;

    public AdminService(JourneyRepository journeyRepository, SystemParameterRepository systemParameterRepository) {
        this.journeyRepository = journeyRepository;
        this.systemParameterRepository = systemParameterRepository;
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


    public List<DailyTrendResponse> getDaylyTrend() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(29);

        LocalDateTime from = start.atStartOfDay();
        LocalDateTime to = today.plusDays(1).atStartOfDay();

        Map<LocalDate, Long> regMap = toLocalDateCountMap(journeyRepository.countRegistrationsByDay(from, to));
        Map<LocalDate, Long> onboardMap = toLocalDateCountMap(journeyRepository.countOnboardedByDay(from, to));

        List<DailyTrendResponse> result = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {
            result.add(new DailyTrendResponse(
                    d,
                    regMap.getOrDefault(d, 0L),
                    onboardMap.getOrDefault(d, 0L)
            ));
        }
        return result;
    }

    public List<WeeklyCompareResponse> getWeeklyCompare() {
        YearMonth current = YearMonth.now();
        YearMonth prev = current.minusMonths(1);

        List<WeeklyCompareResponse> out = new ArrayList<>();
        long[] curWeeks = countByWeekOfMonth(current);
        long[] prevWeeks = countByWeekOfMonth(prev);

        for (int w = 1; w <= 4; w++) {
            out.add(new WeeklyCompareResponse(w, prevWeeks[w], curWeeks[w]));
        }
        return out;
    }

    public List<HourlyActivityResponse> getHourlyActivityLast30Days() {
        LocalDate today = LocalDate.now();
        LocalDateTime from = today.minusDays(29).atStartOfDay();
        LocalDateTime to = today.plusDays(1).atStartOfDay();

        List<Object[]> rows = journeyRepository.countDistinctUsersByHour(from, to);
        Map<Integer, Long> map = new HashMap<>();
        for (Object[] r : rows) {
            int hour = ((Number) r[0]).intValue();
            long c = ((Number) r[1]).longValue();
            map.put(hour, c);
        }

        List<HourlyActivityResponse> out = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            out.add(new HourlyActivityResponse(h, map.getOrDefault(h, 0L)));
        }
        return out;
    }


    public List<MonthlyTrendResponse> getLast12MonthsTrend() {
        YearMonth now = YearMonth.now();
        YearMonth start = now.minusMonths(11);

        LocalDate fromDate = start.atDay(1);
        LocalDate toDateExclusive = now.plusMonths(1).atDay(1);

        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDateExclusive.atStartOfDay();

        List<Object[]> daily = journeyRepository.countUsersByDay(from, to);
        Map<YearMonth, Long> monthMap = new HashMap<>();

        for (Object[] r : daily) {
            LocalDate d = ((java.sql.Date) r[0]).toLocalDate();
            long c = ((Number) r[1]).longValue();
            monthMap.merge(YearMonth.from(d), c, Long::sum);
        }

        List<MonthlyTrendResponse> out = new ArrayList<>();
        for (YearMonth m = start; !m.isAfter(now); m = m.plusMonths(1)) {
            out.add(new MonthlyTrendResponse(m, monthMap.getOrDefault(m, 0L)));
        }
        return out;
    }

    public PeakHourResponse getPeakHourLast30Days() {
        List<HourlyActivityResponse> hours = getHourlyActivityLast30Days();

        int bestHour = 0;
        long best = -1;
        for (HourlyActivityResponse h : hours) {
            if (h.getActiveUsers() > best) {
                best = h.getActiveUsers();
                bestHour = h.getHour();
            }
        }
        return new PeakHourResponse(bestHour, Math.max(best, 0));
    }

    public SystemParameterResponse getSystemParameters() {
        SystemParameter params = systemParameterRepository.findFirstByOrderByIdDesc();
        if (params == null) {
            return new SystemParameterResponse(
                    "Travelio",
                    "https://travelio.com",
                    "contact@travelio.com",
                    "+994500000000",
                    false,
                    false,
                    365
            );
        }
        return new SystemParameterResponse(
                params.getSiteName(),
                params.getSiteUrl(),
                params.getContactEmail(),
                params.getWhatsappNumber(),
                params.getTechnicalMode(),
                params.getDebugMode(),
                params.getDataRetentionDays()
        );
    }

    @Transactional
    public void updateSystemParameters(SystemParameterRequest request) {
        SystemParameter params = systemParameterRepository.findFirstByOrderByIdDesc();
        if (params == null) {
            params = new SystemParameter();
        }
        params.setSiteName(request.getSiteName());
        params.setSiteUrl(request.getSiteUrl());
        params.setContactEmail(request.getContactEmail());
        params.setWhatsappNumber(request.getWhatsappNumber());
        params.setTechnicalMode(request.getTechnicalMode());
        params.setDebugMode(request.getDebugMode());
        params.setDataRetentionDays(request.getDataRetentionDays());
        
        systemParameterRepository.save(params);
    }

    private long[] countByWeekOfMonth(YearMonth ym) {
        LocalDate start = ym.atDay(1);
        LocalDate endExclusive = ym.plusMonths(1).atDay(1);

        LocalDateTime from = start.atStartOfDay();
        LocalDateTime to = endExclusive.atStartOfDay();

        List<LocalDateTime> created = journeyRepository.findCreatedAtBetween(from, to);

        long[] weeks = new long[5];
        for (LocalDateTime t : created) {
            LocalDate d = t.toLocalDate();
            int week = ((d.getDayOfMonth() - 1) / 7) + 1;
            if (week > 4) week = 4;
            weeks[week]++;
        }
        return weeks;
    }

    private Map<LocalDate, Long> toLocalDateCountMap(List<Object[]> rows) {
        Map<LocalDate, Long> map = new HashMap<>();
        for (Object[] r : rows) {
            LocalDate d = ((java.sql.Date) r[0]).toLocalDate();
            long c = ((Number) r[1]).longValue();
            map.put(d, c);
        }
        return map;
    }
}

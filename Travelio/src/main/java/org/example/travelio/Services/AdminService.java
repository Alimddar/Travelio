package org.example.travelio.Services;

import org.example.travelio.DTO.Response.*;
import org.example.travelio.Enums.BudgetType;
import org.example.travelio.Enums.JourneyStatus;
import org.example.travelio.Enums.TravelWith;
import org.example.travelio.Repositories.JourneyRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
public class AdminService {

    private static ZoneId zoneId = ZoneId.of("Asia/Baku");

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

    public List<DailyTrendResponse> getDaylyTrend() {
        LocalDate today = LocalDate.now(zoneId);
        LocalDate start = today.minusDays(29);

        Instant from = start.atStartOfDay(zoneId).toInstant();
        Instant to = today.plusDays(1).atStartOfDay(zoneId).toInstant();

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
        YearMonth current = YearMonth.now(zoneId);
        YearMonth prev = current.minusMonths(1);

        List<WeeklyCompareResponse> out = new ArrayList<>();
        long[] curWeeks = countByWeekOfMonth(current, zoneId);
        long[] prevWeeks = countByWeekOfMonth(prev, zoneId);

        for (int w = 1; w <= 4; w++) {
            out.add(new WeeklyCompareResponse(w, prevWeeks[w], curWeeks[w]));
        }
        return out;
    }

    public List<HourlyActivityResponse> getHourlyActivityLast30Days() {
        LocalDate today = LocalDate.now(zoneId);
        Instant from = today.minusDays(29).atStartOfDay(zoneId).toInstant();
        Instant to = today.plusDays(1).atStartOfDay(zoneId).toInstant();

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
        YearMonth now = YearMonth.now(zoneId);
        YearMonth start = now.minusMonths(11);

        // 12 ay intervalı
        LocalDate fromDate = start.atDay(1);
        LocalDate toDateExclusive = now.plusMonths(1).atDay(1);

        Instant from = fromDate.atStartOfDay(zoneId).toInstant();
        Instant to = toDateExclusive.atStartOfDay(zoneId).toInstant();

        // günlərə count çəkib sonra month-a yığırıq (sadə və DB-dən asılılığı az)
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


    private long[] countByWeekOfMonth(YearMonth ym, ZoneId zoneId) {
        LocalDate start = ym.atDay(1);
        LocalDate endExclusive = ym.plusMonths(1).atDay(1);

        Instant from = start.atStartOfDay(zoneId).toInstant();
        Instant to = endExclusive.atStartOfDay(zoneId).toInstant();

        // daha rahat: createdAt-ları çəkib həftəyə bölürük
        List<Instant> created = journeyRepository.findCreatedAtBetween(from, to);

        long[] weeks = new long[5]; // index 1..4
        for (Instant t : created) {
            LocalDate d = LocalDateTime.ofInstant(t, zoneId).toLocalDate();
            int week = ((d.getDayOfMonth() - 1) / 7) + 1; // 1..5 ola bilər
            if (week > 4) week = 4; // Acceptance: 4 həftə
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

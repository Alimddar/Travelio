package org.example.travelio.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DashboardStatsResponse {
    
    private long totalUsers;
    
    private long onboardedUsers; 
    
    private double activeUsersPercentage;
    
    private double averageJourneyDuration; 
    
    private double overallConversionRate;
}
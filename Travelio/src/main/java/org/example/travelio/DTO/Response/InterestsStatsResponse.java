package org.example.travelio.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestsStatsResponse {

    private List<InterestStat> interests;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterestStat {
        private String name;
        private long userCount;
        private double percentage;
    }
}

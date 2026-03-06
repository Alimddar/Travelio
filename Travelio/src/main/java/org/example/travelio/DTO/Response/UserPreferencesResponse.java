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
public class UserPreferencesResponse {

    private List<PreferenceCount> budgets;
    private List<PreferenceCount> durations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreferenceCount {
        private String label;
        private long count;
    }
}
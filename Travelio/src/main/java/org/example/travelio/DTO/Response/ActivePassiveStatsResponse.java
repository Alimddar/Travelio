package org.example.travelio.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivePassiveStatsResponse {
    private long activeUsers;
    private long passiveUsers;
}
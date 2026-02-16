package org.example.travelio.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TravelResponse {
    private Map<String, Object> tripPlan;
    private List<HotelResponse> suggestedHotels;
    private String status;
}

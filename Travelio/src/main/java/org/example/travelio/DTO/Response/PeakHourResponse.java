package org.example.travelio.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeakHourResponse {
    private int hour;
    private long activeUsers;


}

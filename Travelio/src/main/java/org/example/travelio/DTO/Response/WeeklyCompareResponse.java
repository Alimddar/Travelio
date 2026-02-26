package org.example.travelio.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyCompareResponse {
    private int week;
    private long prevMonthUsers;
    private long currentMonthUsers;


}

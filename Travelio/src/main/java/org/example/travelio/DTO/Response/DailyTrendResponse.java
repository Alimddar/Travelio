package org.example.travelio.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyTrendResponse {
    private LocalDate date;
    private long registrations;
    private long onboarded;


}

package org.example.travelio.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGrowthResponse {
    private String month;
    private long newUserNumber;
    private long completedOnboarding;
}

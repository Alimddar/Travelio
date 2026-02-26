package org.example.travelio.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceUsageDTO {
    private String date;
    private String serviceName;
    private long usageCount;
}

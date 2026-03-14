package org.example.travelio.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemParameterRequest {
    private String siteName;
    private String siteUrl;
    private String contactEmail;
    private String whatsappNumber;
    private Boolean technicalMode;
    private Boolean debugMode;
    private Integer dataRetentionDays;
}

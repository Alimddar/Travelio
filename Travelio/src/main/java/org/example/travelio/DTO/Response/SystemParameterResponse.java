package org.example.travelio.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemParameterResponse {
    private String siteName;
    private String siteUrl;
    private String contactEmail;
    private String whatsappNumber;
    private Boolean technicalMode;
    private Boolean debugMode;
    private Integer dataRetentionDays;
}

package org.example.travelio.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SystemParameterDTO {
    private Long id;
    private String siteName;
    private String siteUrl;
    private String contactEmail;
    private String whatsappNumber;
    private Boolean technicalMode;
    private Boolean debugMode;
    private Integer dataRetentionDays;
}
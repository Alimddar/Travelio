package org.example.travelio.Entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "system_parameters")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SystemParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "site_name", nullable = false)
    private String siteName;

    @Column(name = "site_url", nullable = false)
    private String siteUrl;

    @Column(name = "contact_email", nullable = false)
    private String contactEmail;

    @Column(name = "whatsapp_number")
    private String whatsappNumber;

    @Column(name = "technical_mode")
    private Boolean technicalMode = false;

    @Column(name = "debug_mode")
    private Boolean debugMode = false;

    @Column(name = "data_retention_days")
    private Integer dataRetentionDays = 365;

}
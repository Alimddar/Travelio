package org.example.travelio.Services;


import lombok.RequiredArgsConstructor;
import org.example.travelio.DTO.SystemParameterDTO;
import org.example.travelio.Entities.SystemParameter;
import org.example.travelio.Repositories.SystemParameterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemParameterService {


    private static final long SETTINGS_ID = 1L;

    private final SystemParameterRepository systemParameterRepository;


    public SystemParameterDTO getSystemParameters() {
        SystemParameter s = systemParameterRepository.findById(SETTINGS_ID).orElseGet(this::createDefaultSettings);
        return convertToDTO(s);
    }

    @Transactional
    public SystemParameterDTO updateSystemParameters(SystemParameterDTO dto) {
        SystemParameter s = systemParameterRepository.findById(SETTINGS_ID).orElseGet(this::createDefaultSettings);

        s.setSiteName(dto.getSiteName());
        s.setSiteUrl(dto.getSiteUrl());
        s.setContactEmail(dto.getContactEmail());
        s.setWhatsappNumber(dto.getWhatsappNumber());
        s.setDebugMode(dto.getDebugMode());
        s.setTechnicalMode(dto.getTechnicalMode());
        s.setDataRetentionDays(dto.getDataRetentionDays());

        systemParameterRepository.save(s);
        return convertToDTO(s);
    }



    private SystemParameterDTO convertToDTO(SystemParameter param) {
        return SystemParameterDTO.builder()
                .id(param.getId())
                .siteName(param.getSiteName())
                .siteUrl(param.getSiteUrl())
                .contactEmail(param.getContactEmail())
                .whatsappNumber(param.getWhatsappNumber())
                .technicalMode(param.getTechnicalMode())
                .debugMode(param.getDebugMode())
                .dataRetentionDays(param.getDataRetentionDays())
                .build();
    }
    

    private SystemParameter createDefaultSettings() {
        SystemParameter s = new SystemParameter();
        s.setId(SETTINGS_ID);
        s.setSiteName("Travelia");
        s.setSiteUrl("travelia.az");
        s.setContactEmail("support@travelia.az");
        s.setWhatsappNumber("+994000000000");
        s.setTechnicalMode(false);
        s.setDebugMode(false);
        s.setDataRetentionDays(365);
        return systemParameterRepository.save(s);
    }

}
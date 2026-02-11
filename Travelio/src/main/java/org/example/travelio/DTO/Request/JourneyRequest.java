package org.example.travelio.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class JourneyRequest {

    @NotNull(message = "journey_id is required")
    private Long journeyId;

    @NotNull(message = "selected_guide_id is required")
    private Long selectedGuideId;

    @NotBlank(message = "user_email is required")
    @Email(message = "user_email must be a valid email (text@domain.com)")
    private String userEmail;

    @NotBlank(message = "user_whatsapp is required")
    @Pattern(
            regexp = "^\\+?\\d{9,20}$",
            message = "user_whatsapp must be digits, 9-20 length, may start with +"
    )
    private String userWhatsapp;

}

package org.example.travelio.Controllers;

import org.example.travelio.Services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.travelio.DTO.ItineraryDTO;
import org.example.travelio.Services.PdfService;

@RestController
@RequestMapping("/api/mail")
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);

    private final EmailService emailService;
    private final PdfService pdfService;

    public EmailController(EmailService emailService, PdfService pdfService) {
        this.emailService = emailService;
        this.pdfService = pdfService;
    }

    @PostMapping("/send-plan")
    public ResponseEntity<String> sendItinerary(@RequestBody ItineraryDTO request) {
        try {
            log.info("Generating PDF itinerary for {}", request.getEmail());
            byte[] pdfFile = pdfService.generateItineraryPdf(request.getPlan());

            String subject = "Your Custom Travel Itinerary for Azerbaijan";
            String body = "Hello! \n\nPlease find attached the travel plan we generated for you.\n\nBest Regards,\nTravelio Team";

            emailService.sendEmailWithAttachment(
                    request.getEmail(),
                    subject,
                    body,
                    pdfFile,
                    "Travelio_Itinerary.pdf"
            );

            return ResponseEntity.ok("Itinerary sent successfully to " + request.getEmail());
        } catch (RuntimeException e) {
            log.error("Failed to send itinerary to {}: {}", request.getEmail(), e.getMessage(), e);
            String message = e.getMessage() != null ? e.getMessage() : "Email sending failed.";
            return ResponseEntity.internalServerError().body(message);
        }
    }
}

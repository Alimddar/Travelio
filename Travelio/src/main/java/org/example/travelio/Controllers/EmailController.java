package org.example.travelio.Controllers;

import org.example.travelio.Services.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.travelio.DTO.ItineraryDTO;
import org.example.travelio.Services.PdfService;

@RestController
@RequestMapping("/api/mail")
@CrossOrigin(origins = "http://localhost:5173")
public class EmailController {

    private final EmailService emailService;
    private final PdfService pdfService;

    public EmailController(EmailService emailService, PdfService pdfService) {
        this.emailService = emailService;
        this.pdfService = pdfService;
    }

    @PostMapping("/send-plan")
    public ResponseEntity<String> sendItinerary(@RequestBody ItineraryDTO request) {
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
    }
}
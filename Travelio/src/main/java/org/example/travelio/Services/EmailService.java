package org.example.travelio.Services;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final Resend resend;

    @Value("${resend.from.name}")
    private String fromName;

    @Value("${resend.from.address}")
    private String fromAddress;

    public EmailService(Resend resend) {
        this.resend = resend;
    }

    public void sendEmailWithAttachment(String to, String subject, String body, byte[] pdfBytes, String fileName) {
        if (fromAddress == null || fromAddress.isBlank()) {
            log.error("RESEND_FROM_ADDRESS is not configured");
            throw new RuntimeException("Mail sender is not configured. Set RESEND_FROM_ADDRESS environment variable.");
        }

        String from = fromName != null && !fromName.isBlank()
                ? fromName + " <" + fromAddress + ">"
                : fromAddress;

        log.info("Sending email via Resend from='{}' to={} subject='{}'", from, to, subject);

        try {
            Attachment attachment = Attachment.builder()
                    .fileName(fileName)
                    .content(Base64.getEncoder().encodeToString(pdfBytes))
                    .build();

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(from)
                    .to(to)
                    .subject(subject)
                    .text(body)
                    .attachments(List.of(attachment))
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            log.info("Email sent successfully to {} — Resend id={}", to, response.getId());
        } catch (Exception e) {
            log.error("Resend API failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email via Resend: " + e.getMessage(), e);
        }
    }
}

package org.example.travelio.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ByteArrayResource;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailWithAttachment(String to, String subject, String body, byte[] pdfBytes, String fileName) {
        if (fromEmail == null || fromEmail.isBlank()) {
            log.error("MAIL_USERNAME is not configured — spring.mail.username resolved to blank");
            throw new RuntimeException("Mail sender is not configured. Set MAIL_USERNAME environment variable.");
        }

        log.info("Sending email from={} to={} subject='{}'", fromEmail, to, subject);
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            ByteArrayResource pdfResource = new ByteArrayResource(pdfBytes);
            helper.addAttachment(fileName, pdfResource);

            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (MessagingException | MailException e) {
            String causeMessage = extractRootCauseMessage(e);
            log.error("SMTP email failed: {} | Root cause: {}", e.getMessage(), causeMessage, e);
            throw new RuntimeException("Failed to send email via SMTP: " + causeMessage, e);
        }
    }

    private String extractRootCauseMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() != null ? current.getMessage() : current.getClass().getSimpleName();
    }
}

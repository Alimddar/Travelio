package org.example.travelio.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;

    @Value("${brevo.api.url}")
    private String apiUrl;

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.name:Travelio}")
    private String senderName;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.reply-to.name:}")
    private String replyToName;

    @Value("${brevo.reply-to.email:}")
    private String replyToEmail;

    public EmailService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public void sendEmailWithAttachment(String to, String subject, String body, byte[] pdfBytes, String fileName) {
        validateConfiguration();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sender", Map.of(
                "name", senderName,
                "email", senderEmail
        ));
        payload.put("to", List.of(Map.of("email", to)));
        payload.put("subject", subject);
        payload.put("textContent", body);
        payload.put("attachment", List.of(Map.of(
                "name", fileName,
                "content", Base64.getEncoder().encodeToString(pdfBytes)
        )));
        if (hasText(replyToEmail)) {
            payload.put("replyTo", Map.of(
                    "name", hasText(replyToName) ? replyToName : senderName,
                    "email", replyToEmail
            ));
        }

        try {
            String requestBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("accept", "application/json")
                    .header("api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            log.info("Sending email via Brevo from='{} <{}>' to={} subject='{}'",
                    senderName, senderEmail, to, subject);

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.error("Brevo API failed with status {}: {}", response.statusCode(), response.body());
                throw new RuntimeException("Failed to send email via Brevo: "
                        + response.statusCode() + " " + response.body());
            }

            log.info("Email sent successfully to {} via Brevo", to);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize Brevo request payload.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Email sending was interrupted.", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to call Brevo API: " + e.getMessage(), e);
        }
    }

    private void validateConfiguration() {
        List<String> missing = new java.util.ArrayList<>();

        if (!hasText(apiUrl)) {
            missing.add("BREVO_API_URL");
        }
        if (!hasText(apiKey)) {
            missing.add("BREVO_API_KEY");
        }
        if (!hasText(senderEmail)) {
            missing.add("BREVO_SENDER_EMAIL");
        }
        if (!missing.isEmpty()) {
            throw new RuntimeException("Brevo is not configured. Missing: " + String.join(", ", missing));
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}

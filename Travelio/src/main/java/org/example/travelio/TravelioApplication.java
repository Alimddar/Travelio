package org.example.travelio;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TravelioApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .ignoreIfMalformed()
                .load();

        dotenv.entries().forEach(entry -> {
            String value = entry.getValue();
            boolean unresolvedTemplateValue = value != null && value.contains("${{") && value.contains("}}");
            if (unresolvedTemplateValue) {
                return;
            }

            if (System.getProperty(entry.getKey()) == null && System.getenv(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), value);
            }
        });

        SpringApplication.run(TravelioApplication.class, args);
    }

}

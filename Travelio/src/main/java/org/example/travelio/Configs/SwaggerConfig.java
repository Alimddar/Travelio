package org.example.travelio.Configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI travelioOpenAPI() {
        Server currentServer = new Server()
                .url("/")
                .description("Current Server");

        Info info = new Info()
                .title("Travelio API")
                .version("1.0.0")
                .description("API documentation for Travelio")
                .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT"));

        return new OpenAPI()
                .info(info)
                .servers(List.of(currentServer));
    }
}

package com.weather.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI weatherAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WeatherApp REST API")
                        .description("Enterprise Weather Application with comprehensive monitoring and alerting capabilities")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Weather Team")
                                .email("weather-team@example.com")
                                .url("https://github.com/example/weather-app"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server"),
                        new Server()
                                .url("https://api.weather-app.com")
                                .description("Production server")))
                .tags(List.of(
                        new Tag()
                                .name("Weather")
                                .description("Weather data operations"),
                        new Tag()
                                .name("Alerts")
                                .description("Weather alert management"),
                        new Tag()
                                .name("Forecast")
                                .description("Weather forecast operations"),
                        new Tag()
                                .name("Monitoring")
                                .description("Application monitoring and health checks")));
    }
}

package com.weather.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Spring Boot configuration properties for weather application
 */
@Configuration
@ConfigurationProperties(prefix = "weather")
@Data
@Validated
@Slf4j
public class WeatherConfiguration {

  private String apiKey;

  @NotBlank
  private String apiProvider;

  @Min(1)
  @Max(1440) // Max 24 hours
  private int cacheExpirationMinutes;

  @Min(100)
  @Max(10000)
  private int maxCacheSize;

  @Min(1)
  @Max(16)
  private int maxForecastDays;

  @Min(5)
  @Max(120)
  private int apiTimeoutSeconds;

  @Min(1)
  @Max(10)
  private int apiRetryAttempts;

  private boolean enableMockMode;

}

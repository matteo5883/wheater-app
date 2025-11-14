package com.weather.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Weather API
 */
@Configuration
@ConfigurationProperties(prefix = "weather")
@Data
public class WeatherApiProperties {

  private String apiKey;
  private String baseUrl;
  private String apiProvider;
  private int cacheExpirationMinutes;
  private int maxCacheSize;
  private int maxForecastDays;
  private int apiTimeoutSeconds;
  private int apiRetryAttempts;
  private boolean enableMockMode;
}

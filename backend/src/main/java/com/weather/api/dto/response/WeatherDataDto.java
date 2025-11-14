package com.weather.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Weather Data DTO for API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Weather data information")
public class WeatherDataDto {

  @Schema(description = "Temperature in Celsius", example = "22.5")
  private Double temperature;

  @Schema(description = "Temperature in Fahrenheit", example = "72.5")
  private Double temperatureFahrenheit;

  @Schema(description = "Weather condition", example = "Partly Cloudy")
  private String condition;

  @Schema(description = "Humidity percentage", example = "65")
  private Integer humidity;

  @Schema(description = "Wind speed in km/h", example = "10.5")
  private Double windSpeed;

  @Schema(description = "Data timestamp")
  private LocalDateTime timestamp;

  @Schema(description = "Weather description", example = "Pleasant weather with light clouds")
  private String description;

  @Schema(description = "Whether the data is fresh (not cached)", example = "true")
  private Boolean isFresh;
}

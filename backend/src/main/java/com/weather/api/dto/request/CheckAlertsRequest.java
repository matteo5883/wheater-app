package com.weather.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for checking alerts against weather data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Weather data to check against alerts")
public class CheckAlertsRequest {

  @NotNull(message = "Temperature is required")
  @Schema(description = "Temperature in Celsius", example = "25.0")
  private Double temperature;

  @NotNull(message = "Condition is required")
  @Schema(description = "Weather condition", example = "Sunny")
  private String condition;

  @NotNull(message = "Humidity is required")
  @DecimalMin(value = "0", message = "Humidity must be between 0 and 100")
  @DecimalMax(value = "100", message = "Humidity must be between 0 and 100")
  @Schema(description = "Humidity percentage", example = "65")
  private Integer humidity;

  @NotNull(message = "Wind speed is required")
  @DecimalMin(value = "0.0", message = "Wind speed must be non-negative")
  @Schema(description = "Wind speed in km/h", example = "15.5")
  private Double windSpeed;
}

package com.weather.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Alert check result DTO for API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Result of checking alerts against weather data")
public class AlertCheckResultDto {

  @Schema(description = "List of triggered alerts")
  private List<WeatherAlertDto> triggeredAlerts;

  @Schema(description = "Number of triggered alerts", example = "2")
  private Integer count;

  @Schema(description = "Weather data that was checked")
  private WeatherDataDto weatherData;
}

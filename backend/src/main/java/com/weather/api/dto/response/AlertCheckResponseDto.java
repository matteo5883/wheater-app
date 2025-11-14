package com.weather.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for checking alerts against weather data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Alert check result")
public class AlertCheckResponseDto {

  @Schema(description = "List of triggered alerts")
  private java.util.List<WeatherAlertDto> triggeredAlerts;

  @Schema(description = "Number of triggered alerts", example = "2")
  private Integer count;

  @Schema(description = "Weather data that was checked")
  private WeatherDataDto weatherData;
}

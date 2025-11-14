package com.weather.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Weather Alerts collection DTO for API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Collection of weather alerts")
public class WeatherAlertsDto {

  @Schema(description = "List of weather alerts")
  private List<WeatherAlertDto> alerts;

  @Schema(description = "Total number of alerts", example = "5")
  private Integer count;

  @Schema(description = "Location filter (if applicable)")
  private LocationDto location;

  @Schema(description = "Priority filter (if applicable)", example = "3")
  private Integer priority;
}

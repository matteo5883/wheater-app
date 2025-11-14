package com.weather.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for alerts by priority
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Alerts by priority response")
public class AlertsByPriorityResponseDto {

  @Schema(description = "List of alerts with specified priority")
  private java.util.List<WeatherAlertDto> alerts;

  @Schema(description = "Priority level", example = "3")
  private Integer priority;

  @Schema(description = "Number of alerts", example = "5")
  private Integer count;
}

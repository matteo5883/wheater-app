package com.weather.api.dto.response;

import com.weather.alert.AlertType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Weather Alert DTO for API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Weather alert information")
public class WeatherAlertDto {

  @Schema(description = "Alert unique identifier", example = "alert-123")
  private String id;

  @Schema(description = "Type of alert", example = "HIGH_TEMPERATURE")
  private AlertType alertType;

  @Schema(description = "Alert threshold value", example = "30.0")
  private Double threshold;

  @Schema(description = "Whether the alert is active", example = "true")
  private Boolean active;

  @Schema(description = "Alert priority (1-5, where 1 is highest)", example = "3")
  private Integer priority;

  @Schema(description = "Alert creation timestamp")
  private LocalDateTime createdAt;

  @Schema(description = "Last time the alert was triggered")
  private LocalDateTime lastTriggered;

  @Schema(description = "Custom alert message", example = "Temperature too high!")
  private String message;

  @Schema(description = "Alert description", example = "High temperature alert for Milan, IT")
  private String description;

  @Schema(description = "Whether the alert was recently triggered", example = "false")
  private Boolean wasRecentlyTriggered;

  @Schema(description = "Location for this alert")
  private LocationDto location;

  @Schema(description = "Precipitation conditions (for precipitation alerts only)")
  private List<String> precipitationConditions;
}

package com.weather.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for alert creation operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Alert creation response")
public class AlertCreationResponseDto {

  @Schema(description = "Created alert details")
  private WeatherAlertDto alert;

  @Schema(description = "Success message", example = "Temperature alert created successfully")
  private String message;
}

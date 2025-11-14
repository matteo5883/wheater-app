package com.weather.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for alert deletion operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Alert deletion response")
public class AlertDeletionResponseDto {

  @Schema(description = "Success message", example = "Alert deleted successfully")
  private String message;

  @Schema(description = "ID of deleted alert", example = "alert-123")
  private String alertId;
}

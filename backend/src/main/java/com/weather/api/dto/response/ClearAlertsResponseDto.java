package com.weather.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for clear all alerts operation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Clear alerts response")
public class ClearAlertsResponseDto {

  @Schema(description = "Success message", example = "All alerts cleared successfully")
  private String message;
}

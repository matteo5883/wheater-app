package com.weather.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Health status response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Service health status")
public class HealthStatusResponseDto {

  @Schema(description = "Whether the service is healthy", example = "true")
  private Boolean healthy;

  @Schema(description = "Health status", example = "UP", allowableValues = {"UP", "DOWN"})
  private String status;

  @Schema(description = "Response timestamp", example = "1234567890")
  private Long timestamp;

  @Schema(description = "Error message if any")
  private String error;
}

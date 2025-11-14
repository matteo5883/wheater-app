package com.weather.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cache statistics response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cache statistics response")
public class CacheStatsResponseDto {

  @Schema(description = "Raw statistics string")
  private String statistics;

  @Schema(description = "Parsed cache metrics")
  private CacheMetricsDto cache;
}

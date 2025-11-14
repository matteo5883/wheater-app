package com.weather.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cache metrics DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cache metrics")
public class CacheMetricsDto {

  @Schema(description = "Current cache size", example = "45")
  private Integer size;

  @Schema(description = "Total cache hits", example = "127")
  private Integer hits;

  @Schema(description = "Total cache misses", example = "23")
  private Integer misses;

  @Schema(description = "Cache hit rate percentage", example = "84.67")
  private Double hitRate;
}

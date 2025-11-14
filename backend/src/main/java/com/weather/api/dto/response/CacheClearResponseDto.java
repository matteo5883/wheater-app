package com.weather.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cache clear operation response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cache clear operation response")
public class CacheClearResponseDto {

  @Schema(description = "Operation result message", example = "Cache cleared successfully")
  private String message;
}

package com.weather.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Current weather response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Current weather response")
public class CurrentWeatherResponseDto {

  @Schema(description = "Location information")
  private LocationDto location;

  @Schema(description = "Current weather data")
  private WeatherDataDto weather;
}

package com.weather.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Weather forecast response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Weather forecast response")
public class WeatherForecastResponseDto {

  @Schema(description = "Location information")
  private LocationDto location;

  @Schema(description = "Forecast data for multiple days")
  private List<WeatherDataDto> forecast;

  @Schema(description = "Number of forecast days", example = "5")
  private Integer days;
}


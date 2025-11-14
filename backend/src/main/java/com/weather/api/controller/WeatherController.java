package com.weather.api.controller;

import com.weather.api.dto.response.ApiResponse;
import com.weather.api.dto.response.CacheStatsResponseDto;
import com.weather.api.dto.response.CurrentWeatherResponseDto;
import com.weather.api.dto.response.HealthStatusResponseDto;
import com.weather.api.dto.response.WeatherForecastResponseDto;
import com.weather.api.mapper.WeatherDtoMapper;
import com.weather.model.Location;
import com.weather.model.WeatherData;
import com.weather.service.WeatherService;
import com.weather.service.WeatherServiceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for weather operations
 */
@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Allow frontend from any origin
@Tag(name = "Weather", description = "Weather data operations")
public class WeatherController {

  private final WeatherService weatherService;
  private final WeatherDtoMapper dtoMapper;

  /**
   * Get current weather for a location
   */
  @GetMapping("/current")
  @Operation(
          summary = "Get current weather",
          description = "Retrieve current weather information for a specific location"
  )
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "200",
                  description = "Weather data retrieved successfully",
                  content = @Content(schema = @Schema(implementation = CurrentWeatherResponseDto.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "400",
                  description = "Invalid request parameters",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "503",
                  description = "Weather service unavailable",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          )
  })
  public ResponseEntity<ApiResponse<CurrentWeatherResponseDto>> getCurrentWeather(
          @Parameter(description = "City name", example = "Milan", required = true)
          @RequestParam String city,
          @Parameter(description = "Country code (ISO 3166-1 alpha-2)", example = "IT", required = true)
          @RequestParam String country,
          @Parameter(description = "Latitude coordinate", example = "45.4642")
          @RequestParam(required = false) Double latitude,
          @Parameter(description = "Longitude coordinate", example = "9.1900")
          @RequestParam(required = false) Double longitude) {

    try {
      Location location = dtoMapper.createLocation(city, country, latitude, longitude);
      WeatherData weather = weatherService.getCurrentWeather(location);

      CurrentWeatherResponseDto responseDto = CurrentWeatherResponseDto.builder()
              .location(dtoMapper.toLocationDto(location))
              .weather(dtoMapper.toWeatherDataDto(weather))
              .build();

      return ResponseEntity.ok(ApiResponse.success(responseDto));

    } catch (IllegalArgumentException e) {
      log.warn("Invalid request parameters: {}", e.getMessage());
      return ResponseEntity.badRequest()
              .body(ApiResponse.error("Invalid parameters", e.getMessage()));
    } catch (WeatherServiceException e) {
      log.error("Weather service error: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
              .body(ApiResponse.error("Service unavailable", e.getMessage()));
    } catch (Exception e) {
      log.error("Unexpected error getting current weather", e);
      return ResponseEntity.internalServerError()
              .body(ApiResponse.error("Internal server error", "An unexpected error occurred"));
    }
  }

  /**
   * Get weather forecast for multiple days
   */
  @GetMapping("/forecast")
  @Operation(
          summary = "Get weather forecast",
          description = "Retrieve weather forecast for multiple days"
  )
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "200",
                  description = "Forecast data retrieved successfully",
                  content = @Content(schema = @Schema(implementation = WeatherForecastResponseDto.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "400",
                  description = "Invalid request parameters",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "503",
                  description = "Weather service unavailable",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          )
  })
  public ResponseEntity<ApiResponse<WeatherForecastResponseDto>> getWeatherForecast(
          @Parameter(description = "City name", example = "Milan", required = true)
          @RequestParam String city,
          @Parameter(description = "Country code (ISO 3166-1 alpha-2)", example = "IT", required = true)
          @RequestParam String country,
          @Parameter(description = "Number of forecast days", example = "5")
          @RequestParam(defaultValue = "5") @Min(1) @Max(14) int days,
          @Parameter(description = "Latitude coordinate", example = "45.4642")
          @RequestParam(required = false) Double latitude,
          @Parameter(description = "Longitude coordinate", example = "9.1900")
          @RequestParam(required = false) Double longitude) {

    try {
      Location location = dtoMapper.createLocation(city, country, latitude, longitude);
      WeatherData[] forecast = weatherService.getWeatherForecast(location, days);

      WeatherForecastResponseDto responseDto = dtoMapper.toWeatherForecastResponseDto(location, forecast, days);
      return ResponseEntity.ok(ApiResponse.success(responseDto));

    } catch (IllegalArgumentException e) {
      log.warn("Invalid forecast request: {}", e.getMessage());
      return ResponseEntity.badRequest()
              .body(ApiResponse.error("Invalid parameters", e.getMessage()));
    } catch (WeatherServiceException e) {
      log.error("Weather service error for forecast: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
              .body(ApiResponse.error("Service unavailable", e.getMessage()));
    } catch (Exception e) {
      log.error("Unexpected error getting forecast", e);
      return ResponseEntity.internalServerError()
              .body(ApiResponse.error("Internal server error", "An unexpected error occurred"));
    }
  }

}

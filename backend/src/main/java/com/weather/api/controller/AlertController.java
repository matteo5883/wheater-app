package com.weather.api.controller;

import com.weather.alert.AlertType;
import com.weather.alert.WeatherAlert;
import com.weather.alert.WeatherAlertSystem;
import com.weather.api.dto.request.CheckAlertsRequest;
import com.weather.api.dto.request.CreatePrecipitationAlertRequest;
import com.weather.api.dto.request.CreateTemperatureAlertRequest;
import com.weather.api.dto.request.CreateWindAlertRequest;
import com.weather.api.dto.response.AlertCheckResponseDto;
import com.weather.api.dto.response.AlertCreationResponseDto;
import com.weather.api.dto.response.AlertDeletionResponseDto;
import com.weather.api.dto.response.AlertsByPriorityResponseDto;
import com.weather.api.dto.response.ApiResponse;
import com.weather.api.dto.response.ClearAlertsResponseDto;
import com.weather.api.dto.response.WeatherAlertsDto;
import com.weather.api.mapper.WeatherDtoMapper;
import com.weather.model.Location;
import com.weather.model.WeatherData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for weather alerts management
 */
@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Alerts", description = "Weather alert management operations")
public class AlertController {

  private final WeatherAlertSystem alertSystem;
  private final WeatherDtoMapper dtoMapper;

  /**
   * Get all active alerts
   */
  @GetMapping
  @Operation(
          summary = "Get all active alerts",
          description = "Retrieve all currently active weather alerts"
  )
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "200",
                  description = "Active alerts retrieved successfully",
                  content = @Content(schema = @Schema(implementation = WeatherAlertsDto.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "500",
                  description = "Internal server error",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          )
  })
  public ResponseEntity<ApiResponse<WeatherAlertsDto>> getActiveAlerts() {
    try {
      List<WeatherAlert> alerts = alertSystem.getActiveAlerts();
      WeatherAlertsDto responseDto = dtoMapper.toWeatherAlertsDto(alerts);

      return ResponseEntity.ok(ApiResponse.success(responseDto));
    } catch (Exception e) {
      log.error("Error getting active alerts", e);
      return ResponseEntity.internalServerError()
              .body(ApiResponse.error("Failed to retrieve alerts"));
    }
  }

  /**
   * Get alerts for a specific location
   */
  @GetMapping("/location")
  @Operation(
          summary = "Get alerts for location",
          description = "Retrieve all active alerts for a specific location"
  )
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "200",
                  description = "Location alerts retrieved successfully",
                  content = @Content(schema = @Schema(implementation = WeatherAlertsDto.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "500",
                  description = "Internal server error",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          )
  })
  public ResponseEntity<ApiResponse<WeatherAlertsDto>> getAlertsForLocation(
          @Parameter(description = "City name", example = "Milan")
          @RequestParam String city,
          @Parameter(description = "Country code (ISO 3166-1 alpha-2)", example = "IT")
          @RequestParam String country,
          @Parameter(description = "Latitude coordinate", example = "45.4642")
          @RequestParam(required = false) Double latitude,
          @Parameter(description = "Longitude coordinate", example = "9.1900")
          @RequestParam(required = false) Double longitude) {

    try {
      Location location = dtoMapper.createLocation(city, country, latitude, longitude);
      List<WeatherAlert> alerts = alertSystem.getActiveAlertsForLocation(location);
      WeatherAlertsDto responseDto = dtoMapper.toWeatherAlertsDto(alerts, location);

      return ResponseEntity.ok(ApiResponse.success(responseDto));
    } catch (Exception e) {
      log.error("Error getting alerts for location", e);
      return ResponseEntity.internalServerError()
              .body(ApiResponse.error("Failed to retrieve location alerts"));
    }
  }

  /**
   * Create a temperature alert
   */
  @PostMapping("/temperature")
  @Operation(
          summary = "Create temperature alert",
          description = "Create a new temperature-based weather alert"
  )
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "201",
                  description = "Temperature alert created successfully",
                  content = @Content(schema = @Schema(implementation = AlertCreationResponseDto.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "400",
                  description = "Invalid request parameters",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "500",
                  description = "Internal server error",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          )
  })
  public ResponseEntity<ApiResponse<AlertCreationResponseDto>> createTemperatureAlert(
          @Valid @RequestBody CreateTemperatureAlertRequest request) {
    try {
      Location location = dtoMapper.createLocation(
              request.getCity(),
              request.getCountry(),
              request.getLatitude(),
              request.getLongitude()
      );

      AlertType alertType = "high".equalsIgnoreCase(request.getAlertType()) ?
              AlertType.HIGH_TEMPERATURE : AlertType.LOW_TEMPERATURE;

      WeatherAlert alert = alertSystem.createTemperatureAlert(location, request.getThreshold(), alertType);
      alert.setPriority(request.getPriority());

      if (request.getMessage() != null) {
        alert.setMessage(request.getMessage());
      }

      alertSystem.addAlert(alert);

      AlertCreationResponseDto responseDto = dtoMapper.toAlertCreationResponseDto(
              alert, "Temperature alert created successfully"
      );

      return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(responseDto));

    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
              .body(ApiResponse.error("Invalid parameters", e.getMessage()));
    } catch (Exception e) {
      log.error("Error creating temperature alert", e);
      return ResponseEntity.internalServerError()
              .body(ApiResponse.error("Failed to create alert"));
    }
  }

  /**
   * Create a wind speed alert
   */
  @PostMapping("/wind")
  @Operation(
          summary = "Create wind speed alert",
          description = "Create a new wind speed-based weather alert"
  )
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "201",
                  description = "Wind speed alert created successfully",
                  content = @Content(schema = @Schema(implementation = AlertCreationResponseDto.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "400",
                  description = "Invalid request parameters",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "500",
                  description = "Internal server error",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          )
  })
  public ResponseEntity<ApiResponse<AlertCreationResponseDto>> createWindAlert(
          @Valid @RequestBody CreateWindAlertRequest request) {
    try {
      Location location = dtoMapper.createLocation(
              request.getCity(),
              request.getCountry(),
              request.getLatitude(),
              request.getLongitude()
      );

      WeatherAlert alert = alertSystem.createWindSpeedAlert(location, request.getThreshold());
      alert.setPriority(request.getPriority());

      if (request.getMessage() != null) {
        alert.setMessage(request.getMessage());
      }

      alertSystem.addAlert(alert);

      AlertCreationResponseDto responseDto = dtoMapper.toAlertCreationResponseDto(
              alert, "Wind speed alert created successfully"
      );

      return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(responseDto));

    } catch (Exception e) {
      log.error("Error creating wind alert", e);
      return ResponseEntity.internalServerError()
              .body(ApiResponse.error("Failed to create wind alert"));
    }
  }

  /**
   * Create a precipitation alert
   */
  @PostMapping("/precipitation")
  @Operation(
          summary = "Create precipitation alert",
          description = "Create a new precipitation-based weather alert"
  )
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "201",
                  description = "Precipitation alert created successfully",
                  content = @Content(schema = @Schema(implementation = AlertCreationResponseDto.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "400",
                  description = "Invalid request parameters",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "500",
                  description = "Internal server error",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          )
  })
  public ResponseEntity<ApiResponse<AlertCreationResponseDto>> createPrecipitationAlert(
          @Valid @RequestBody CreatePrecipitationAlertRequest request) {
    try {
      Location location = dtoMapper.createLocation(
              request.getCity(),
              request.getCountry(),
              request.getLatitude(),
              request.getLongitude()
      );

      WeatherAlert alert = alertSystem.createPrecipitationAlert(location, request.getConditions());
      alert.setPriority(request.getPriority());

      if (request.getMessage() != null) {
        alert.setMessage(request.getMessage());
      }

      alertSystem.addAlert(alert);

      AlertCreationResponseDto responseDto = dtoMapper.toAlertCreationResponseDto(
              alert, "Precipitation alert created successfully"
      );

      return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(responseDto));

    } catch (Exception e) {
      log.error("Error creating precipitation alert", e);
      return ResponseEntity.internalServerError()
              .body(ApiResponse.error("Failed to create precipitation alert"));
    }
  }

  /**
   * Delete an alert by ID
   */
  @DeleteMapping("/{alertId}")
  @Operation(
          summary = "Delete alert by ID",
          description = "Delete a specific weather alert by its ID"
  )
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "200",
                  description = "Alert deleted successfully",
                  content = @Content(schema = @Schema(implementation = AlertDeletionResponseDto.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "404",
                  description = "Alert not found",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "500",
                  description = "Internal server error",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          )
  })
  public ResponseEntity<ApiResponse<AlertDeletionResponseDto>> deleteAlert(
          @Parameter(description = "Alert ID", example = "alert-123")
          @PathVariable String alertId) {
    try {
      List<WeatherAlert> activeAlerts = alertSystem.getActiveAlerts();
      WeatherAlert alertToRemove = activeAlerts.stream()
              .filter(alert -> alert.getId().equals(alertId))
              .findFirst()
              .orElse(null);

      if (alertToRemove == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Alert not found", "No alert with ID: " + alertId));
      }

      alertSystem.removeAlert(alertToRemove);

      AlertDeletionResponseDto responseDto = dtoMapper.toAlertDeletionResponseDto(
              alertId, "Alert deleted successfully"
      );

      return ResponseEntity.ok(ApiResponse.success(responseDto));

    } catch (Exception e) {
      log.error("Error deleting alert", e);
      return ResponseEntity.internalServerError()
              .body(ApiResponse.error("Failed to delete alert"));
    }
  }

  /**
   * Check alerts against current weather data
   */
  @PostMapping("/check")
  @Operation(
          summary = "Check alerts against weather data",
          description = "Check all active alerts against provided weather data and return triggered alerts"
  )
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "200",
                  description = "Alerts checked successfully",
                  content = @Content(schema = @Schema(implementation = AlertCheckResponseDto.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "400",
                  description = "Invalid weather data",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "500",
                  description = "Internal server error",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          )
  })
  public ResponseEntity<ApiResponse<AlertCheckResponseDto>> checkAlerts(
          @Valid @RequestBody CheckAlertsRequest request) {
    try {
      WeatherData weatherData = dtoMapper.toWeatherData(request);
      List<WeatherAlert> triggeredAlerts = alertSystem.checkAlerts(weatherData);

      AlertCheckResponseDto responseDto = dtoMapper.toAlertCheckResponseDto(triggeredAlerts, weatherData);

      return ResponseEntity.ok(ApiResponse.success(responseDto));

    } catch (Exception e) {
      log.error("Error checking alerts", e);
      return ResponseEntity.internalServerError()
              .body(ApiResponse.error("Failed to check alerts"));
    }
  }

  /**
   * Get alerts by priority
   */
  @GetMapping("/priority/{priority}")
  @Operation(
          summary = "Get alerts by priority",
          description = "Retrieve all alerts with a specific priority level"
  )
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "200",
                  description = "Priority alerts retrieved successfully",
                  content = @Content(schema = @Schema(implementation = AlertsByPriorityResponseDto.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "400",
                  description = "Invalid priority value",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "500",
                  description = "Internal server error",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          )
  })
  public ResponseEntity<ApiResponse<AlertsByPriorityResponseDto>> getAlertsByPriority(
          @Parameter(description = "Priority level (1-5, where 1 is highest)", example = "3")
          @PathVariable @Min(1) @Max(5) int priority) {
    try {
      List<WeatherAlert> alerts = alertSystem.getAlertsByPriority(priority);
      AlertsByPriorityResponseDto responseDto = dtoMapper.toAlertsByPriorityResponseDto(alerts, priority);

      return ResponseEntity.ok(ApiResponse.success(responseDto));
    } catch (Exception e) {
      log.error("Error getting alerts by priority", e);
      return ResponseEntity.internalServerError()
              .body(ApiResponse.error("Failed to retrieve alerts by priority"));
    }
  }

  /**
   * Clear all alerts
   */
  @DeleteMapping
  @Operation(
          summary = "Clear all alerts",
          description = "Remove all active weather alerts"
  )
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "200",
                  description = "All alerts cleared successfully",
                  content = @Content(schema = @Schema(implementation = ClearAlertsResponseDto.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  responseCode = "500",
                  description = "Internal server error",
                  content = @Content(schema = @Schema(implementation = ApiResponse.class))
          )
  })
  public ResponseEntity<ApiResponse<ClearAlertsResponseDto>> clearAllAlerts() {
    try {
      alertSystem.clearAllAlerts();
      ClearAlertsResponseDto responseDto = dtoMapper.toClearAlertsResponseDto("All alerts cleared successfully");

      return ResponseEntity.ok(ApiResponse.success(responseDto));
    } catch (Exception e) {
      log.error("Error clearing all alerts", e);
      return ResponseEntity.internalServerError()
              .body(ApiResponse.error("Failed to clear alerts"));
    }
  }

}

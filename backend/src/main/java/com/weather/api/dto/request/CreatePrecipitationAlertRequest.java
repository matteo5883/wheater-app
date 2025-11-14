package com.weather.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating precipitation alerts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Precipitation alert creation request")
public class CreatePrecipitationAlertRequest {

  @NotBlank(message = "City is required")
  @Schema(description = "City name", example = "Milan")
  private String city;

  @NotBlank(message = "Country is required")
  @Schema(description = "Country code (ISO 3166-1 alpha-2)", example = "IT")
  private String country;

  @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
  @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
  @Schema(description = "Latitude coordinate", example = "45.4642")
  private Double latitude;

  @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
  @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
  @Schema(description = "Longitude coordinate", example = "9.1900")
  private Double longitude;

  @NotEmpty(message = "At least one weather condition is required")
  @Schema(description = "List of weather conditions to alert on", example = "[\"Rain\", \"Snow\", \"Storm\"]")
  private List<String> conditions;

  @Min(value = 1, message = "Priority must be between 1 and 5")
  @Max(value = 5, message = "Priority must be between 1 and 5")
  @Schema(description = "Alert priority (1-5, where 1 is highest)", example = "3")
  @Builder.Default
  private Integer priority = 3;

  @Schema(description = "Custom alert message", example = "Precipitation expected in Milan!")
  private String message;
}

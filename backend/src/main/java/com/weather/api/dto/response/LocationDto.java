package com.weather.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Location DTO for API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Location information")
public class LocationDto {

  @Schema(description = "City name", example = "Milan")
  private String city;

  @Schema(description = "Country code (ISO 3166-1 alpha-2)", example = "IT")
  private String country;

  @Schema(description = "Latitude coordinate", example = "45.4642")
  private Double latitude;

  @Schema(description = "Longitude coordinate", example = "9.1900")
  private Double longitude;

  @Schema(description = "Full location name", example = "Milan, IT")
  public String getFullName() {
    return city + ", " + country;
  }
}

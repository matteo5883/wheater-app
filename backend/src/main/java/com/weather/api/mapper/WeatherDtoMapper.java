package com.weather.api.mapper;

import com.weather.alert.WeatherAlert;
import com.weather.api.dto.request.CheckAlertsRequest;
import com.weather.api.dto.response.AlertCheckResponseDto;
import com.weather.api.dto.response.AlertCreationResponseDto;
import com.weather.api.dto.response.AlertDeletionResponseDto;
import com.weather.api.dto.response.AlertsByPriorityResponseDto;
import com.weather.api.dto.response.CacheClearResponseDto;
import com.weather.api.dto.response.CacheMetricsDto;
import com.weather.api.dto.response.CacheStatsResponseDto;
import com.weather.api.dto.response.ClearAlertsResponseDto;
import com.weather.api.dto.response.HealthStatusResponseDto;
import com.weather.api.dto.response.LocationDto;
import com.weather.api.dto.response.WeatherAlertDto;
import com.weather.api.dto.response.WeatherAlertsDto;
import com.weather.api.dto.response.WeatherDataDto;
import com.weather.api.dto.response.WeatherForecastResponseDto;
import com.weather.model.Location;
import com.weather.model.WeatherData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Mapper class for converting between domain models and DTOs
 */
@Component
public class WeatherDtoMapper {

  /**
   * Convert Location to LocationDto
   */
  public LocationDto toLocationDto(Location location) {
    if (location == null) {
      return null;
    }

    return LocationDto.builder()
            .city(location.getCity())
            .country(location.getCountry())
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .build();
  }

  /**
   * Convert WeatherData to WeatherDataDto
   */
  public WeatherDataDto toWeatherDataDto(WeatherData weatherData) {
    if (weatherData == null) {
      return null;
    }

    return WeatherDataDto.builder()
            .temperature(weatherData.getTemperature())
            .temperatureFahrenheit(weatherData.getTemperatureInFahrenheit())
            .condition(weatherData.getCondition())
            .humidity(weatherData.getHumidity())
            .windSpeed(weatherData.getWindSpeed())
            .timestamp(weatherData.getTimestamp())
            .description(weatherData.getDescription())
            .isFresh(weatherData.isFresh())
            .build();
  }

  /**
   * Convert WeatherAlert to WeatherAlertDto
   */
  public WeatherAlertDto toWeatherAlertDto(WeatherAlert alert) {
    if (alert == null) {
      return null;
    }

    return WeatherAlertDto.builder()
            .id(alert.getId())
            .alertType(alert.getAlertType())
            .threshold(alert.getThreshold())
            .active(alert.isActive())
            .priority(alert.getPriority())
            .createdAt(alert.getCreatedAt())
            .lastTriggered(alert.getLastTriggered())
            .message(alert.getMessage())
            .description(alert.getDescription())
            .wasRecentlyTriggered(alert.wasRecentlyTriggered())
            .location(toLocationDto(alert.getLocation()))
            .precipitationConditions(alert.getPrecipitationConditions())
            .build();
  }

  /**
   * Convert list of WeatherAlert to WeatherAlertsDto
   */
  public WeatherAlertsDto toWeatherAlertsDto(List<WeatherAlert> alerts) {
    return toWeatherAlertsDto(alerts, null, null);
  }

  /**
   * Convert list of WeatherAlert to WeatherAlertsDto with location filter
   */
  public WeatherAlertsDto toWeatherAlertsDto(List<WeatherAlert> alerts, Location location) {
    return toWeatherAlertsDto(alerts, location, null);
  }

  /**
   * Convert list of WeatherAlert to WeatherAlertsDto with filters
   */
  public WeatherAlertsDto toWeatherAlertsDto(List<WeatherAlert> alerts, Location location, Integer priority) {
    if (alerts == null) {
      alerts = List.of();
    }

    return WeatherAlertsDto.builder()
            .alerts(alerts.stream()
                    .map(this::toWeatherAlertDto)
                    .collect(Collectors.toList()))
            .count(alerts.size())
            .location(toLocationDto(location))
            .priority(priority)
            .build();
  }

  /**
   * Convert WeatherData array to list of WeatherDataDto
   */
  public List<WeatherDataDto> toWeatherDataDtoList(WeatherData[] weatherDataArray) {
    if (weatherDataArray == null) {
      return List.of();
    }

    return Stream.of(weatherDataArray)
            .map(this::toWeatherDataDto)
            .collect(Collectors.toList());
  }

  /**
   * Convert WeatherData array to WeatherForecastResponseDto
   */
  public WeatherForecastResponseDto toWeatherForecastResponseDto(Location location, WeatherData[] forecast, int days) {
    return WeatherForecastResponseDto.builder()
            .location(toLocationDto(location))
            .forecast(toWeatherDataDtoList(forecast))
            .days(days)
            .build();
  }

  /**
   * Parse cache statistics string to CacheStatsResponseDto
   */
  public CacheStatsResponseDto toCacheStatsResponseDto(String stats) {
    return CacheStatsResponseDto.builder()
            .statistics(stats)
            .cache(parseCacheStats(stats))
            .build();
  }

  /**
   * Create health status response DTO
   */
  public HealthStatusResponseDto toHealthStatusResponseDto(boolean healthy, String error) {
    return HealthStatusResponseDto.builder()
            .healthy(healthy)
            .status(healthy ? "UP" : "DOWN")
            .timestamp(System.currentTimeMillis())
            .error(error)
            .build();
  }

  /**
   * Create cache clear response DTO
   */
  public CacheClearResponseDto toCacheClearResponseDto(String message) {
    return CacheClearResponseDto.builder()
            .message(message)
            .build();
  }

  private CacheMetricsDto parseCacheStats(String stats) {
    // Parse "Cache Statistics: Size=45, Hits=127, Misses=23, Hit Rate=84.67%"
    try {
      var builder = CacheMetricsDto.builder();
      String[] parts = stats.split(", ");

      for (String part : parts) {
        if (part.contains("Size=")) {
          builder.size(Integer.parseInt(part.split("=")[1]));
        } else if (part.contains("Hits=")) {
          builder.hits(Integer.parseInt(part.split("=")[1]));
        } else if (part.contains("Misses=")) {
          builder.misses(Integer.parseInt(part.split("=")[1]));
        } else if (part.contains("Hit Rate=")) {
          String rate = part.split("=")[1].replace("%", "");
          builder.hitRate(Double.parseDouble(rate));
        }
      }

      return builder.build();
    } catch (Exception e) {
      // Return empty metrics if parsing fails
      return CacheMetricsDto.builder().build();
    }
  }

  /**
   * Create Location from city, country and optional coordinates
   */
  public Location createLocation(String city, String country, Double latitude, Double longitude) {
    if (latitude != null && longitude != null) {
      return new Location(city, country, latitude, longitude);
    } else {
      // Use approximate coordinates for major cities (simplified geocoding)
      double[] coords = getApproximateCoordinates(city, country);
      return new Location(city, country, coords[0], coords[1]);
    }
  }

  private double[] getApproximateCoordinates(String city, String country) {
    // Simplified coordinate lookup for demo purposes
    String key = (city + "_" + country).toLowerCase();

    switch (key) {
      case "milan_it":
        return new double[]{45.4642, 9.1900};
      case "rome_it":
        return new double[]{41.9028, 12.4964};
      case "florence_it":
        return new double[]{43.7696, 11.2558};
      case "venice_it":
        return new double[]{45.4408, 12.3155};
      case "naples_it":
        return new double[]{40.8518, 14.2681};
      case "turin_it":
        return new double[]{45.0703, 7.6869};
      case "london_gb":
        return new double[]{51.5074, -0.1278};
      case "paris_fr":
        return new double[]{48.8566, 2.3522};
      case "madrid_es":
        return new double[]{40.4168, -3.7038};
      case "berlin_de":
        return new double[]{52.5200, 13.4050};
      case "new york_us":
        return new double[]{40.7128, -74.0060};
      case "tokyo_jp":
        return new double[]{35.6762, 139.6503};
      default:
        // Generate approximate coordinates based on hash
        int hash = key.hashCode();
        double lat = 45.0 + (hash % 40 - 20); // Range roughly 25-65
        double lon = 9.0 + (hash % 60 - 30);  // Range roughly -21 to 39
        return new double[]{lat, lon};
    }
  }

  /**
   * Convert CheckAlertsRequest to WeatherData
   */
  public WeatherData toWeatherData(CheckAlertsRequest request) {
    WeatherData weatherData = new WeatherData();
    weatherData.setTemperature(request.getTemperature());
    weatherData.setCondition(request.getCondition());
    weatherData.setHumidity(request.getHumidity());
    weatherData.setWindSpeed(request.getWindSpeed());
    return weatherData;
  }

  /**
   * Create AlertCreationResponseDto
   */
  public AlertCreationResponseDto toAlertCreationResponseDto(WeatherAlert alert, String message) {
    return AlertCreationResponseDto.builder()
            .alert(toWeatherAlertDto(alert))
            .message(message)
            .build();
  }

  /**
   * Create AlertDeletionResponseDto
   */
  public AlertDeletionResponseDto toAlertDeletionResponseDto(String alertId, String message) {
    return AlertDeletionResponseDto.builder()
            .alertId(alertId)
            .message(message)
            .build();
  }

  /**
   * Create AlertCheckResponseDto
   */
  public AlertCheckResponseDto toAlertCheckResponseDto(List<WeatherAlert> triggeredAlerts, WeatherData weatherData) {
    return AlertCheckResponseDto.builder()
            .triggeredAlerts(triggeredAlerts.stream()
                    .map(this::toWeatherAlertDto)
                    .collect(Collectors.toList()))
            .count(triggeredAlerts.size())
            .weatherData(toWeatherDataDto(weatherData))
            .build();
  }

  /**
   * Create AlertsByPriorityResponseDto
   */
  public AlertsByPriorityResponseDto toAlertsByPriorityResponseDto(List<WeatherAlert> alerts, int priority) {
    return AlertsByPriorityResponseDto.builder()
            .alerts(alerts.stream()
                    .map(this::toWeatherAlertDto)
                    .collect(Collectors.toList()))
            .priority(priority)
            .count(alerts.size())
            .build();
  }

  /**
   * Create ClearAlertsResponseDto
   */
  public ClearAlertsResponseDto toClearAlertsResponseDto(String message) {
    return ClearAlertsResponseDto.builder()
            .message(message)
            .build();
  }
}

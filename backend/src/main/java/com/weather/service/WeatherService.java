package com.weather.service;

import com.weather.model.Location;
import com.weather.model.WeatherData;
import com.weather.monitoring.circuit.CircuitBreaker;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Spring Boot service for weather operations with caching, circuit breaker and monitoring
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

  private final WeatherApiClient apiClient;
  private final CacheManager cacheManager;
  private final CircuitBreaker circuitBreaker;
  private final MeterRegistry meterRegistry;
  private static final int MAX_FORECAST_DAYS = 14;


  /**
   * Gets current weather for a location with Spring caching and monitoring
   *
   * @param location the location to get weather for
   * @return current weather data
   * @throws IllegalArgumentException if location is null
   * @throws WeatherServiceException  if API call fails
   */
  @Cacheable(value = "weather-current",
          key = "#location.city + '_' + #location.country")
  public WeatherData getCurrentWeather(Location location) {
    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null");
    }

    // Record metrics
    Timer apiTimer = Timer.builder("weather.api.calls.duration")
            .description("Weather API call duration")
            .tag("operation", "getCurrentWeather")
            .register(meterRegistry);

    try {
      return apiTimer.recordCallable(() -> {
        try {
          return circuitBreaker.execute(() -> {
            try {
              WeatherData data = apiClient.getCurrentWeather(location);
              // Record success
              Counter.builder("weather.api.calls.total")
                      .description("Total weather API calls")
                      .tag("operation", "getCurrentWeather")
                      .tag("status", "success")
                      .register(meterRegistry)
                      .increment();
              return data;
            } catch (RuntimeException e) {
              Counter.builder("weather.api.calls.total")
                      .description("Total weather API calls")
                      .tag("operation", "getCurrentWeather")
                      .tag("status", "failure")
                      .register(meterRegistry)
                      .increment();
              if (e instanceof NetworkException) {
                throw new WeatherServiceException(
                        "Network error while fetching weather data", e);
              }
              throw new WeatherServiceException(
                      "Error fetching weather data: " + e.getMessage(), e);
            }
          });

        } catch (Exception e) {
          Counter.builder("weather.api.calls.total")
                  .description("Total weather API calls")
                  .tag("operation", "getCurrentWeather")
                  .tag("status", "error")
                  .register(meterRegistry)
                  .increment();
          throw e;
        }
      });
    } catch (Exception e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      }
      throw new WeatherServiceException("Unexpected error", e);
    }
  }

  /**
   * Gets weather forecast for multiple days
   *
   * @param location the location to get forecast for
   * @param days     number of days to forecast
   * @return array of weather data for each day
   * @throws IllegalArgumentException if location is null or days is invalid
   * @throws WeatherServiceException  if API call fails
   */
  @Cacheable(value = "weather-forecast",
          key = "#location.city + '_' + #location.country + '_' + #days")
  public WeatherData[] getWeatherForecast(Location location, int days) {
    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null");
    }
    if (days <= 0) {
      throw new IllegalArgumentException(
              "Number of forecast days must be positive");
    }

    int requestDays = Math.min(days, MAX_FORECAST_DAYS);

    try {
      return apiClient.getWeatherForecast(location, requestDays);

    } catch (RuntimeException e) {
      if (e instanceof NetworkException) {
        throw new WeatherServiceException(
                "Network error while fetching forecast", e);
      }
      throw new WeatherServiceException(
              "Error fetching forecast: " + e.getMessage(), e);
    }
  }

  /**
   * Clears the weather cache
   */
  @CacheEvict(value = {"weather-current", "weather-forecast"},
          allEntries = true)
  public void clearCache() {
    log.info("Weather caches cleared via Spring Cache");
  }

  /**
   * Gets cache statistics
   *
   * @return cache statistics string
   */
  public String getCacheStatistics() {
    StringBuilder stats = new StringBuilder();
    stats.append("Spring Cache Statistics:\n");

    cacheManager.getCacheNames().forEach(cacheName -> {
      org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
      if (cache != null) {
        stats.append("Cache: ").append(cacheName).append(" - Active\n");
      }
    });

    return stats.toString();
  }

  /**
   * Checks if the service is healthy by testing API connectivity
   *
   * @return true if service is healthy
   */
  public boolean isHealthy() {
    try {
      // Try to fetch weather for a test location
      Location testLocation = new Location("London", "GB", 51.5074, -0.1278);
      getCurrentWeather(testLocation);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}

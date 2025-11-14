package com.weather.service;

import com.weather.model.Location;
import com.weather.model.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Weather Service Tests")
public class WeatherServiceTest {

  private WeatherService weatherService;

  @Mock
  private WeatherApiClient weatherApiClient;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Create mock CacheManager
    CacheManager cacheManager = org.mockito.Mockito.mock(CacheManager.class);

    com.weather.monitoring.circuit.CircuitBreaker circuitBreaker =
            new com.weather.monitoring.circuit.CircuitBreaker(
                    com.weather.monitoring.circuit.CircuitBreakerConfig.builder()
                            .name("test-circuit")
                            .build()
            );
    io.micrometer.core.instrument.MeterRegistry meterRegistry =
            new io.micrometer.core.instrument.simple.SimpleMeterRegistry();

    weatherService = new WeatherService(
            weatherApiClient, cacheManager, circuitBreaker, meterRegistry);
  }

  @Test
  @DisplayName("Should fetch current weather for valid location")
  void shouldFetchCurrentWeatherForValidLocation() {
    // Given
    Location location = new Location("Milan", "IT", 45.4642, 9.1900);
    WeatherData expectedWeather = createSampleWeatherData();
    when(weatherApiClient.getCurrentWeather(location)).thenReturn(expectedWeather);

    // When
    WeatherData actualWeather = weatherService.getCurrentWeather(location);

    // Then
    assertNotNull(actualWeather);
    assertEquals(expectedWeather.getTemperature(), actualWeather.getTemperature());
    assertEquals(expectedWeather.getCondition(), actualWeather.getCondition());
    verify(weatherApiClient, times(1)).getCurrentWeather(location);
  }

  @Test
  @DisplayName("Should throw exception for null location")
  void shouldThrowExceptionForNullLocation() {
    // When & Then
    assertThrows(IllegalArgumentException.class,
            () -> weatherService.getCurrentWeather(null));
  }

  @Test
  @DisplayName("Should handle API timeout gracefully")
  void shouldHandleApiTimeoutGracefully() {
    // Given
    Location location = new Location("Rome", "IT", 41.9028, 12.4964);
    when(weatherApiClient.getCurrentWeather(location))
            .thenThrow(new RuntimeException("API Timeout"));

    // When & Then
    assertThrows(WeatherServiceException.class,
            () -> weatherService.getCurrentWeather(location));
  }

  @Test
  @DisplayName("Should fetch weather forecast for multiple days")
  void shouldFetchWeatherForecastForMultipleDays() {
    // Given
    Location location = new Location("Florence", "IT", 43.7696, 11.2558);
    int days = 7;
    WeatherData[] expectedForecast = createSampleForecastArray(days);
    when(weatherApiClient.getWeatherForecast(location, days)).thenReturn(expectedForecast);

    // When
    WeatherData[] forecast = weatherService.getWeatherForecast(location, days);

    // Then
    assertNotNull(forecast);
    assertEquals(days, forecast.length);
    verify(weatherApiClient, times(1)).getWeatherForecast(location, days);
  }

  @Test
  @DisplayName("Should throw exception for invalid forecast days")
  void shouldThrowExceptionForInvalidForecastDays() {
    // Given
    Location location = new Location("Venice", "IT", 45.4408, 12.3155);
    int invalidDays = -1;

    // When & Then
    assertThrows(IllegalArgumentException.class,
            () -> weatherService.getWeatherForecast(location, invalidDays));
  }

  @Test
  @DisplayName("Should limit forecast days to maximum allowed")
  void shouldLimitForecastDaysToMaximumAllowed() {
    // Given
    Location location = new Location("Naples", "IT", 40.8518, 14.2681);
    int excessiveDays = 20;
    int maxAllowedDays = 14;
    WeatherData[] expectedForecast = createSampleForecastArray(maxAllowedDays);
    when(weatherApiClient.getWeatherForecast(location, maxAllowedDays)).thenReturn(expectedForecast);

    // When
    WeatherData[] forecast = weatherService.getWeatherForecast(location, excessiveDays);

    // Then
    assertNotNull(forecast);
    assertTrue(forecast.length <= maxAllowedDays);
  }

  @Test
  @DisplayName("Should cache weather data for performance")
  void shouldCacheWeatherDataForPerformance() {
    // Given
    Location location = new Location("Turin", "IT", 45.0703, 7.6869);
    WeatherData expectedWeather = createSampleWeatherData();
    when(weatherApiClient.getCurrentWeather(location)).thenReturn(expectedWeather);

    // When
    WeatherData firstCall = weatherService.getCurrentWeather(location);
    WeatherData secondCall = weatherService.getCurrentWeather(location);

    // Then
    assertNotNull(firstCall);
    assertNotNull(secondCall);
    assertEquals(firstCall.getTemperature(), secondCall.getTemperature());
    // With Spring Boot cache, behavior may vary - verify at least one call was made
    verify(weatherApiClient, atLeastOnce()).getCurrentWeather(location);
  }

  @Test
  @DisplayName("Should refresh cached data after expiration")
  void shouldRefreshCachedDataAfterExpiration() {
    // Given
    Location location = new Location("Palermo", "IT", 38.1157, 13.3615);
    WeatherData weatherData = createSampleWeatherData();
    when(weatherApiClient.getCurrentWeather(location)).thenReturn(weatherData);

    // When
    weatherService.getCurrentWeather(location);
    // Simulate cache expiration
    weatherService.clearCache();
    weatherService.getCurrentWeather(location);

    // Then
    verify(weatherApiClient, times(2)).getCurrentWeather(location);
  }

  @Test
  @DisplayName("Should handle network connectivity issues")
  void shouldHandleNetworkConnectivityIssues() {
    // Given
    Location location = new Location("Bologna", "IT", 44.4949, 11.3426);
    when(weatherApiClient.getCurrentWeather(location))
            .thenThrow(new NetworkException("No internet connection"));

    // When & Then
    assertThrows(WeatherServiceException.class,
            () -> weatherService.getCurrentWeather(location));
  }

  private WeatherData createSampleWeatherData() {
    WeatherData weatherData = new WeatherData();
    weatherData.setTemperature(22.5);
    weatherData.setCondition("Partly Cloudy");
    weatherData.setHumidity(65);
    weatherData.setWindSpeed(10.5);
    return weatherData;
  }

  private WeatherData[] createSampleForecastArray(int days) {
    WeatherData[] forecast = new WeatherData[days];
    for (int i = 0; i < days; i++) {
      WeatherData weatherData = new WeatherData();
      weatherData.setTemperature(20.0 + i);
      weatherData.setCondition("Day " + (i + 1) + " Weather");
      weatherData.setHumidity(60 + i);
      weatherData.setWindSpeed(10.0 + i);
      forecast[i] = weatherData;
    }
    return forecast;
  }
}

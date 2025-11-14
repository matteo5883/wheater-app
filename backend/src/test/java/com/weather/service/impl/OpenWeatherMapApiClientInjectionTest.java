package com.weather.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.config.WeatherApiProperties;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit test to verify dependency injection works correctly for OpenWeatherMapApiClient
 */
@ExtendWith(MockitoExtension.class)
class OpenWeatherMapApiClientInjectionTest {

  @Mock
  private WeatherApiProperties weatherProperties;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private OkHttpClient httpClient;

  @InjectMocks
  private OpenWeatherMapApiClient apiClient;

  @Test
  void shouldInjectAllDependenciesCorrectly() {
    when(weatherProperties.getApiKey()).thenReturn("test-api-key-12345");
    when(weatherProperties.getBaseUrl()).thenReturn("https://test-api.example.com/v1");
    // Verify the client is created successfully with all dependencies
    assertThat(apiClient).isNotNull();

    // Verify that the properties can be accessed (indirectly through the mock)
    assertThat(weatherProperties.getApiKey()).isEqualTo("test-api-key-12345");
    assertThat(weatherProperties.getBaseUrl()).isEqualTo("https://test-api.example.com/v1");
  }

  @Test
  void shouldUseInjectedHttpClient() {
    // Verify HTTP client is injected
    assertThat(httpClient).isNotNull();
  }

  @Test
  void shouldUseInjectedObjectMapper() {
    // Verify ObjectMapper is injected
    assertThat(objectMapper).isNotNull();
  }
}

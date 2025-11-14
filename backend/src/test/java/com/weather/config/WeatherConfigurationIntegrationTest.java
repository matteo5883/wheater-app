package com.weather.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to verify Spring Boot configuration loading works correctly
 */
@SpringBootTest
@ActiveProfiles("test")
class WeatherConfigurationIntegrationTest {

    @Autowired
    private WeatherApiProperties weatherProperties;

    @Test
    void shouldLoadTestConfiguration() {
        // Verify test configuration is loaded
        assertThat(weatherProperties).isNotNull();
        assertThat(weatherProperties.getApiKey()).isEqualTo("test-api-key-12345");
        assertThat(weatherProperties.getBaseUrl()).isEqualTo("https://test-api.example.com/v1");
        assertThat(weatherProperties.isEnableMockMode()).isTrue();
        assertThat(weatherProperties.getApiTimeoutSeconds()).isEqualTo(10);
    }

    @Test
    void shouldHaveCorrectCacheConfiguration() {
        // Verify cache configuration for tests
        assertThat(weatherProperties.getCacheExpirationMinutes()).isEqualTo(5);
        assertThat(weatherProperties.getMaxCacheSize()).isEqualTo(100);
        assertThat(weatherProperties.getMaxForecastDays()).isEqualTo(7);
    }

    @Test
    void shouldHaveCorrectRetryConfiguration() {
        // Verify retry configuration for tests
        assertThat(weatherProperties.getApiRetryAttempts()).isEqualTo(1);
    }
}

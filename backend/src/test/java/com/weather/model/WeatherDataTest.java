package com.weather.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Weather Data Tests")
public class WeatherDataTest {

    private WeatherData weatherData;

    @BeforeEach
    void setUp() {
        weatherData = new WeatherData();
    }

    @Test
    @DisplayName("Should create weather data with valid temperature")
    void shouldCreateWeatherDataWithValidTemperature() {
        // Given
        double temperature = 25.5;
        String condition = "Sunny";

        // When
        weatherData.setTemperature(temperature);
        weatherData.setCondition(condition);

        // Then
        assertEquals(temperature, weatherData.getTemperature());
        assertEquals(condition, weatherData.getCondition());
    }

    @Test
    @DisplayName("Should handle extreme cold temperatures")
    void shouldHandleExtremeColdTemperatures() {
        // Given
        double extremeCold = -50.0;

        // When & Then
        assertDoesNotThrow(() -> weatherData.setTemperature(extremeCold));
        assertEquals(extremeCold, weatherData.getTemperature());
    }

    @Test
    @DisplayName("Should handle extreme hot temperatures")
    void shouldHandleExtremeHotTemperatures() {
        // Given
        double extremeHot = 60.0;

        // When & Then
        assertDoesNotThrow(() -> weatherData.setTemperature(extremeHot));
        assertEquals(extremeHot, weatherData.getTemperature());
    }

    @Test
    @DisplayName("Should throw exception for invalid temperature beyond physical limits")
    void shouldThrowExceptionForInvalidTemperature() {
        // Given
        double invalidTemperature = -300.0; // Below absolute zero

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> weatherData.setTemperature(invalidTemperature));
    }

    @Test
    @DisplayName("Should handle null condition gracefully")
    void shouldHandleNullConditionGracefully() {
        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> weatherData.setCondition(null));
    }

    @Test
    @DisplayName("Should handle empty condition string")
    void shouldHandleEmptyConditionString() {
        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> weatherData.setCondition(""));
    }

    @Test
    @DisplayName("Should validate humidity range")
    void shouldValidateHumidityRange() {
        // Given
        int validHumidity = 65;
        int invalidHumidityHigh = 150;
        int invalidHumidityLow = -10;

        // When & Then
        assertDoesNotThrow(() -> weatherData.setHumidity(validHumidity));
        assertEquals(validHumidity, weatherData.getHumidity());

        assertThrows(IllegalArgumentException.class,
                () -> weatherData.setHumidity(invalidHumidityHigh));
        assertThrows(IllegalArgumentException.class,
                () -> weatherData.setHumidity(invalidHumidityLow));
    }

    @Test
    @DisplayName("Should validate wind speed is non-negative")
    void shouldValidateWindSpeedIsNonNegative() {
        // Given
        double validWindSpeed = 15.5;
        double invalidWindSpeed = -5.0;

        // When & Then
        assertDoesNotThrow(() -> weatherData.setWindSpeed(validWindSpeed));
        assertEquals(validWindSpeed, weatherData.getWindSpeed());

        assertThrows(IllegalArgumentException.class,
                () -> weatherData.setWindSpeed(invalidWindSpeed));
    }

    @Test
    @DisplayName("Should calculate temperature in Fahrenheit correctly")
    void shouldCalculateTemperatureInFahrenheitCorrectly() {
        // Given
        double celsiusTemp = 25.0;
        double expectedFahrenheit = 77.0;

        // When
        weatherData.setTemperature(celsiusTemp);

        // Then
        assertEquals(expectedFahrenheit, weatherData.getTemperatureInFahrenheit(), 0.01);
    }

    @Test
    @DisplayName("Should handle freezing point conversion correctly")
    void shouldHandleFreezingPointConversionCorrectly() {
        // Given
        double freezingCelsius = 0.0;
        double expectedFahrenheit = 32.0;

        // When
        weatherData.setTemperature(freezingCelsius);

        // Then
        assertEquals(expectedFahrenheit, weatherData.getTemperatureInFahrenheit(), 0.01);
    }

    @Test
    @DisplayName("Should set temperature from Fahrenheit correctly")
    void shouldSetTemperatureFromFahrenheitCorrectly() {
        // Given
        double fahrenheitTemp = 77.0;
        double expectedCelsius = 25.0;

        // When
        weatherData.setTemperatureFromFahrenheit(fahrenheitTemp);

        // Then
        assertEquals(expectedCelsius, weatherData.getTemperature(), 0.01);
    }

    @Test
    @DisplayName("Should handle freezing point when setting from Fahrenheit")
    void shouldHandleFreezingPointWhenSettingFromFahrenheit() {
        // Given
        double freezingFahrenheit = 32.0;
        double expectedCelsius = 0.0;

        // When
        weatherData.setTemperatureFromFahrenheit(freezingFahrenheit);

        // Then
        assertEquals(expectedCelsius, weatherData.getTemperature(), 0.01);
    }

    @Test
    @DisplayName("Should handle boiling point when setting from Fahrenheit")
    void shouldHandleBoilingPointWhenSettingFromFahrenheit() {
        // Given
        double boilingFahrenheit = 212.0;
        double expectedCelsius = 100.0;

        // When
        weatherData.setTemperatureFromFahrenheit(boilingFahrenheit);

        // Then
        assertEquals(expectedCelsius, weatherData.getTemperature(), 0.01);
    }

    @Test
    @DisplayName("Should handle negative Fahrenheit temperatures correctly")
    void shouldHandleNegativeFahrenheitTemperaturesCorrectly() {
        // Given
        double negativeFahrenheit = -40.0;
        double expectedCelsius = -40.0; // -40°F = -40°C (special case where they're equal)

        // When
        weatherData.setTemperatureFromFahrenheit(negativeFahrenheit);

        // Then
        assertEquals(expectedCelsius, weatherData.getTemperature(), 0.01);
    }

    @Test
    @DisplayName("Should handle extreme cold Fahrenheit temperatures")
    void shouldHandleExtremeColdFahrenheitTemperatures() {
        // Given
        double extremeColdFahrenheit = -100.0;
        double expectedCelsius = -73.33; // (-100 - 32) * 5/9 = -73.33°C

        // When
        weatherData.setTemperatureFromFahrenheit(extremeColdFahrenheit);

        // Then
        assertEquals(expectedCelsius, weatherData.getTemperature(), 0.01);
    }

    @Test
    @DisplayName("Should handle room temperature conversion from Fahrenheit")
    void shouldHandleRoomTemperatureConversionFromFahrenheit() {
        // Given
        double roomTempFahrenheit = 68.0;
        double expectedCelsius = 20.0;

        // When
        weatherData.setTemperatureFromFahrenheit(roomTempFahrenheit);

        // Then
        assertEquals(expectedCelsius, weatherData.getTemperature(), 0.01);
    }

    @Test
    @DisplayName("Should throw exception when setting invalid temperature via Fahrenheit")
    void shouldThrowExceptionWhenSettingInvalidTemperatureViaFahrenheit() {
        // Given - Temperature below absolute zero in Fahrenheit (-459.67°F = -273.15°C)
        double belowAbsoluteZeroFahrenheit = -500.0;

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> weatherData.setTemperatureFromFahrenheit(belowAbsoluteZeroFahrenheit));
    }

    @Test
    @DisplayName("Should throw exception when setting temperature above maximum via Fahrenheit")
    void shouldThrowExceptionWhenSettingTemperatureAboveMaximumViaFahrenheit() {
        // Given - Temperature above 100°C in Fahrenheit (212°F = 100°C, so 250°F > 100°C)
        double aboveMaximumFahrenheit = 250.0;

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> weatherData.setTemperatureFromFahrenheit(aboveMaximumFahrenheit));
    }

    @Test
    @DisplayName("Should handle absolute zero in Fahrenheit correctly")
    void shouldHandleAbsoluteZeroInFahrenheitCorrectly() {
        // Given - Absolute zero in Fahrenheit
        double absoluteZeroFahrenheit = -459.67;
        double expectedCelsius = -273.15;

        // When
        weatherData.setTemperatureFromFahrenheit(absoluteZeroFahrenheit);

        // Then
        assertEquals(expectedCelsius, weatherData.getTemperature(), 0.01);
    }

    @Test
    @DisplayName("Should maintain precision in Fahrenheit to Celsius conversion")
    void shouldMaintainPrecisionInFahrenheitToCelsiusConversion() {
        // Given
        double preciseFahrenheit = 98.6; // Normal human body temperature
        double expectedCelsius = 37.0;

        // When
        weatherData.setTemperatureFromFahrenheit(preciseFahrenheit);

        // Then
        assertEquals(expectedCelsius, weatherData.getTemperature(), 0.01);
    }
}

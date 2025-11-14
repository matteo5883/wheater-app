package com.weather.alert;

import com.weather.model.Location;
import com.weather.model.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Weather Alert System Tests")
public class WeatherAlertSystemTest {

    private WeatherAlertSystem alertSystem;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        alertSystem = new WeatherAlertSystem();
        testLocation = new Location("Milan", "IT", 45.4642, 9.1900);
    }

    @Test
    @DisplayName("Should create temperature alert correctly")
    void shouldCreateTemperatureAlertCorrectly() {
        // Given
        double threshold = 35.0;
        AlertType alertType = AlertType.HIGH_TEMPERATURE;

        // When
        WeatherAlert alert = alertSystem.createTemperatureAlert(testLocation, threshold, alertType);

        // Then
        assertNotNull(alert);
        assertEquals(testLocation, alert.getLocation());
        assertEquals(threshold, alert.getThreshold());
        assertEquals(alertType, alert.getAlertType());
        assertTrue(alert.isActive());
    }

    @Test
    @DisplayName("Should trigger high temperature alert")
    void shouldTriggerHighTemperatureAlert() {
        // Given
        double threshold = 30.0;
        WeatherAlert alert = alertSystem.createTemperatureAlert(
                testLocation, threshold, AlertType.HIGH_TEMPERATURE);

        WeatherData hotWeather = createWeatherDataWithTemperature(35.0);

        // When
        boolean shouldTrigger = alertSystem.shouldTriggerAlert(alert, hotWeather);

        // Then
        assertTrue(shouldTrigger);
    }

    @Test
    @DisplayName("Should trigger low temperature alert")
    void shouldTriggerLowTemperatureAlert() {
        // Given
        double threshold = 5.0;
        WeatherAlert alert = alertSystem.createTemperatureAlert(
                testLocation, threshold, AlertType.LOW_TEMPERATURE);

        WeatherData coldWeather = createWeatherDataWithTemperature(0.0);

        // When
        boolean shouldTrigger = alertSystem.shouldTriggerAlert(alert, coldWeather);

        // Then
        assertTrue(shouldTrigger);
    }

    @Test
    @DisplayName("Should not trigger alert when temperature is within threshold")
    void shouldNotTriggerAlertWhenTemperatureIsWithinThreshold() {
        // Given
        double threshold = 30.0;
        WeatherAlert alert = alertSystem.createTemperatureAlert(
                testLocation, threshold, AlertType.HIGH_TEMPERATURE);

        WeatherData normalWeather = createWeatherDataWithTemperature(25.0);

        // When
        boolean shouldTrigger = alertSystem.shouldTriggerAlert(alert, normalWeather);

        // Then
        assertFalse(shouldTrigger);
    }

    @Test
    @DisplayName("Should create precipitation alert correctly")
    void shouldCreatePrecipitationAlertCorrectly() {
        // Given
        List<String> precipitationConditions = List.of("Rain", "Heavy Rain", "Thunderstorm");

        // When
        WeatherAlert alert = alertSystem.createPrecipitationAlert(
                testLocation, precipitationConditions);

        // Then
        assertNotNull(alert);
        assertEquals(AlertType.PRECIPITATION, alert.getAlertType());
        assertEquals(precipitationConditions, alert.getPrecipitationConditions());
    }

    @Test
    @DisplayName("Should trigger precipitation alert for matching condition")
    void shouldTriggerPrecipitationAlertForMatchingCondition() {
        // Given
        List<String> precipitationConditions = List.of("Rain", "Heavy Rain");
        WeatherAlert alert = alertSystem.createPrecipitationAlert(
                testLocation, precipitationConditions);

        WeatherData rainyWeather = createWeatherDataWithCondition("Heavy Rain");

        // When
        boolean shouldTrigger = alertSystem.shouldTriggerAlert(alert, rainyWeather);

        // Then
        assertTrue(shouldTrigger);
    }

    @Test
    @DisplayName("Should not trigger precipitation alert for non-matching condition")
    void shouldNotTriggerPrecipitationAlertForNonMatchingCondition() {
        // Given
        List<String> precipitationConditions = List.of("Rain", "Heavy Rain");
        WeatherAlert alert = alertSystem.createPrecipitationAlert(
                testLocation, precipitationConditions);

        WeatherData sunnyWeather = createWeatherDataWithCondition("Sunny");

        // When
        boolean shouldTrigger = alertSystem.shouldTriggerAlert(alert, sunnyWeather);

        // Then
        assertFalse(shouldTrigger);
    }

    @Test
    @DisplayName("Should create wind speed alert correctly")
    void shouldCreateWindSpeedAlertCorrectly() {
        // Given
        double windSpeedThreshold = 50.0; // km/h

        // When
        WeatherAlert alert = alertSystem.createWindSpeedAlert(testLocation, windSpeedThreshold);

        // Then
        assertNotNull(alert);
        assertEquals(AlertType.HIGH_WIND_SPEED, alert.getAlertType());
        assertEquals(windSpeedThreshold, alert.getThreshold());
    }

    @Test
    @DisplayName("Should trigger wind speed alert for high winds")
    void shouldTriggerWindSpeedAlertForHighWinds() {
        // Given
        double threshold = 40.0;
        WeatherAlert alert = alertSystem.createWindSpeedAlert(testLocation, threshold);

        WeatherData windyWeather = createWeatherDataWithWindSpeed(60.0);

        // When
        boolean shouldTrigger = alertSystem.shouldTriggerAlert(alert, windyWeather);

        // Then
        assertTrue(shouldTrigger);
    }

    @Test
    @DisplayName("Should add alert to active alerts list")
    void shouldAddAlertToActiveAlertsList() {
        // Given
        WeatherAlert alert = alertSystem.createTemperatureAlert(
                testLocation, 30.0, AlertType.HIGH_TEMPERATURE);

        // When
        alertSystem.addAlert(alert);

        // Then
        List<WeatherAlert> activeAlerts = alertSystem.getActiveAlerts();
        assertTrue(activeAlerts.contains(alert));
    }

    @Test
    @DisplayName("Should remove alert from active alerts list")
    void shouldRemoveAlertFromActiveAlertsList() {
        // Given
        WeatherAlert alert = alertSystem.createTemperatureAlert(
                testLocation, 30.0, AlertType.HIGH_TEMPERATURE);
        alertSystem.addAlert(alert);

        // When
        alertSystem.removeAlert(alert);

        // Then
        List<WeatherAlert> activeAlerts = alertSystem.getActiveAlerts();
        assertFalse(activeAlerts.contains(alert));
    }

    @Test
    @DisplayName("Should not add duplicate alerts")
    void shouldNotAddDuplicateAlerts() {
        // Given
        WeatherAlert alert1 = alertSystem.createTemperatureAlert(
                testLocation, 30.0, AlertType.HIGH_TEMPERATURE);
        WeatherAlert alert2 = alertSystem.createTemperatureAlert(
                testLocation, 30.0, AlertType.HIGH_TEMPERATURE);

        // When
        alertSystem.addAlert(alert1);
        alertSystem.addAlert(alert2);

        // Then
        List<WeatherAlert> activeAlerts = alertSystem.getActiveAlerts();
        assertEquals(1, activeAlerts.size());
    }

    @Test
    @DisplayName("Should check all active alerts against current weather")
    void shouldCheckAllActiveAlertsAgainstCurrentWeather() {
        // Given
        WeatherAlert tempAlert = alertSystem.createTemperatureAlert(
                testLocation, 30.0, AlertType.HIGH_TEMPERATURE);
        WeatherAlert windAlert = alertSystem.createWindSpeedAlert(testLocation, 40.0);

        alertSystem.addAlert(tempAlert);
        alertSystem.addAlert(windAlert);

        WeatherData extremeWeather = new WeatherData();
        extremeWeather.setTemperature(35.0);
        extremeWeather.setWindSpeed(50.0);
        extremeWeather.setCondition("Hot and Windy");
        extremeWeather.setHumidity(30);

        // When
        List<WeatherAlert> triggeredAlerts = alertSystem.checkAlerts(extremeWeather);

        // Then
        assertEquals(2, triggeredAlerts.size());
        assertTrue(triggeredAlerts.contains(tempAlert));
        assertTrue(triggeredAlerts.contains(windAlert));
    }

    @Test
    @DisplayName("Should throw exception for invalid alert threshold")
    void shouldThrowExceptionForInvalidAlertThreshold() {
        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> alertSystem.createTemperatureAlert(testLocation, -300.0, AlertType.HIGH_TEMPERATURE));
    }

    @Test
    @DisplayName("Should deactivate alert correctly")
    void shouldDeactivateAlertCorrectly() {
        // Given
        WeatherAlert alert = alertSystem.createTemperatureAlert(
                testLocation, 30.0, AlertType.HIGH_TEMPERATURE);
        alertSystem.addAlert(alert);

        // When
        alertSystem.deactivateAlert(alert);

        // Then
        assertFalse(alert.isActive());
        assertFalse(alertSystem.getActiveAlerts().contains(alert));
    }

    private WeatherData createWeatherDataWithTemperature(double temperature) {
        WeatherData weatherData = new WeatherData();
        weatherData.setTemperature(temperature);
        weatherData.setCondition("Clear");
        weatherData.setHumidity(50);
        weatherData.setWindSpeed(10.0);
        return weatherData;
    }

    private WeatherData createWeatherDataWithCondition(String condition) {
        WeatherData weatherData = new WeatherData();
        weatherData.setTemperature(20.0);
        weatherData.setCondition(condition);
        weatherData.setHumidity(60);
        weatherData.setWindSpeed(15.0);
        return weatherData;
    }

    private WeatherData createWeatherDataWithWindSpeed(double windSpeed) {
        WeatherData weatherData = new WeatherData();
        weatherData.setTemperature(22.0);
        weatherData.setCondition("Windy");
        weatherData.setHumidity(45);
        weatherData.setWindSpeed(windSpeed);
        return weatherData;
    }
}

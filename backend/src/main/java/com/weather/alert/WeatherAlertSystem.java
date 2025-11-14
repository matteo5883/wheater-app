package com.weather.alert;

import com.weather.model.Location;
import com.weather.model.WeatherData;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * System for managing weather alerts and checking conditions
 */
@Component
public class WeatherAlertSystem {

  @Getter
  private final List<WeatherAlert> activeAlerts;
  private final Map<String, WeatherAlert> alertsById;

  public WeatherAlertSystem() {
    this.activeAlerts = new CopyOnWriteArrayList<>();
    this.alertsById = new ConcurrentHashMap<>();
  }

  /**
   * Creates a temperature alert
   *
   * @param location  the location
   * @param threshold temperature threshold
   * @param alertType HIGH_TEMPERATURE or LOW_TEMPERATURE
   * @return created alert
   */
  public WeatherAlert createTemperatureAlert(Location location, double threshold, AlertType alertType) {
    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null");
    }
    if (alertType != AlertType.HIGH_TEMPERATURE && alertType != AlertType.LOW_TEMPERATURE) {
      throw new IllegalArgumentException("Alert type must be HIGH_TEMPERATURE or LOW_TEMPERATURE");
    }
    if (threshold < -300) {
      throw new IllegalArgumentException("Invalid temperature threshold");
    }

    return new WeatherAlert(location, alertType, threshold);
  }

  /**
   * Creates a precipitation alert
   *
   * @param location                the location
   * @param precipitationConditions conditions to alert on
   * @return created alert
   */
  public WeatherAlert createPrecipitationAlert(Location location, List<String> precipitationConditions) {
    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null");
    }
    if (precipitationConditions == null || precipitationConditions.isEmpty()) {
      throw new IllegalArgumentException("Precipitation conditions cannot be null or empty");
    }

    WeatherAlert alert = new WeatherAlert(location, AlertType.PRECIPITATION);
    alert.setPrecipitationConditions(precipitationConditions);
    return alert;
  }

  /**
   * Creates a wind speed alert
   *
   * @param location           the location
   * @param windSpeedThreshold wind speed threshold in km/h
   * @return created alert
   */
  public WeatherAlert createWindSpeedAlert(Location location, double windSpeedThreshold) {
    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null");
    }
    if (windSpeedThreshold < 0) {
      throw new IllegalArgumentException("Wind speed threshold cannot be negative");
    }

    return new WeatherAlert(location, AlertType.HIGH_WIND_SPEED, windSpeedThreshold);
  }

  /**
   * Adds an alert to the active alerts list
   *
   * @param alert the alert to add
   */
  public void addAlert(WeatherAlert alert) {
    if (alert == null) {
      throw new IllegalArgumentException("Alert cannot be null");
    }

    if (!activeAlerts.contains(alert)) {
      activeAlerts.add(alert);
      alertsById.put(alert.getId(), alert);
    }
  }

  /**
   * Removes an alert from the active alerts list
   *
   * @param alert the alert to remove
   */
  public void removeAlert(WeatherAlert alert) {
    if (alert != null) {
      activeAlerts.remove(alert);
      alertsById.remove(alert.getId());
    }
  }

  /**
   * Deactivates an alert
   *
   * @param alert the alert to deactivate
   */
  public void deactivateAlert(WeatherAlert alert) {
    if (alert != null) {
      alert.deactivate();
      removeAlert(alert);
    }
  }


  /**
   * Gets active alerts for a specific location
   *
   * @param location the location
   * @return list of alerts for the location
   */
  public List<WeatherAlert> getActiveAlertsForLocation(Location location) {
    return activeAlerts.stream()
            .filter(alert -> alert.getLocation().equals(location))
            .collect(Collectors.toList());
  }

  /**
   * Checks if an alert should be triggered based on current weather
   *
   * @param alert       the alert to check
   * @param weatherData current weather data
   * @return true if alert should trigger
   */
  public boolean shouldTriggerAlert(WeatherAlert alert, WeatherData weatherData) {
    if (alert == null || weatherData == null || !alert.isActive()) {
      return false;
    }

    switch (alert.getAlertType()) {
      case HIGH_TEMPERATURE:
        return weatherData.getTemperature() > alert.getThreshold();

      case LOW_TEMPERATURE:
        return weatherData.getTemperature() < alert.getThreshold();

      case HIGH_WIND_SPEED:
        return weatherData.getWindSpeed() > alert.getThreshold();

      case HIGH_HUMIDITY:
        return weatherData.getHumidity() > alert.getThreshold();

      case LOW_HUMIDITY:
        return weatherData.getHumidity() < alert.getThreshold();

      case PRECIPITATION:
        List<String> conditions = alert.getPrecipitationConditions();
        if (conditions != null) {
          String currentCondition = weatherData.getCondition();
          return conditions.stream()
                  .anyMatch(condition -> currentCondition.toLowerCase()
                          .contains(condition.toLowerCase()));
        }
        return false;

      default:
        return false;
    }
  }

  /**
   * Checks all active alerts against current weather data
   *
   * @param weatherData current weather data
   * @return list of triggered alerts
   */
  public List<WeatherAlert> checkAlerts(WeatherData weatherData) {
    if (weatherData == null) {
      return Collections.emptyList();
    }

    List<WeatherAlert> triggeredAlerts = new ArrayList<>();

    for (WeatherAlert alert : activeAlerts) {
      if (shouldTriggerAlert(alert, weatherData)) {
        alert.trigger();
        triggeredAlerts.add(alert);
      }
    }

    return triggeredAlerts;
  }

  /**
   * Gets alerts by priority level
   *
   * @param priority priority level (1-5)
   * @return list of alerts with the specified priority
   */
  public List<WeatherAlert> getAlertsByPriority(int priority) {
    return activeAlerts.stream()
            .filter(alert -> alert.getPriority() == priority)
            .collect(Collectors.toList());
  }

  /**
   * Clears all alerts
   */
  public void clearAllAlerts() {
    activeAlerts.clear();
    alertsById.clear();
  }
}

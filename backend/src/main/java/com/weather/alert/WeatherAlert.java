package com.weather.alert;

import com.weather.model.Location;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a weather alert for specific conditions
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"location", "alertType", "threshold", "precipitationConditions"})
@ToString
public class WeatherAlert {

  private String id;
  private Location location;
  private AlertType alertType;
  private double threshold;
  private List<String> precipitationConditions;
  private boolean active;
  private LocalDateTime createdAt;
  private LocalDateTime lastTriggered;
  private String message;
  private int priority;

  public WeatherAlert() {
    this.id = UUID.randomUUID().toString();
    this.createdAt = LocalDateTime.now();
    this.active = true;
    this.priority = 3;
  }

  public WeatherAlert(Location location, AlertType alertType) {
    this();
    setLocation(location);
    setAlertType(alertType);
  }

  public WeatherAlert(Location location, AlertType alertType, double threshold) {
    this(location, alertType);
    setThreshold(threshold);
  }

  public void setLocation(Location location) {
    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null");
    }
    this.location = location;
  }

  public void setAlertType(AlertType alertType) {
    if (alertType == null) {
      throw new IllegalArgumentException("Alert type cannot be null");
    }
    this.alertType = alertType;
  }

  public void setThreshold(double threshold) {
    if (alertType == AlertType.HIGH_TEMPERATURE || alertType == AlertType.LOW_TEMPERATURE) {
      if (threshold < -100 || threshold > 100) {
        throw new IllegalArgumentException("Temperature threshold must be between -100 and 100 degrees");
      }
    } else if (alertType == AlertType.HIGH_WIND_SPEED) {
      if (threshold < 0) {
        throw new IllegalArgumentException("Wind speed threshold cannot be negative");
      }
    } else if (alertType == AlertType.HIGH_HUMIDITY || alertType == AlertType.LOW_HUMIDITY) {
      if (threshold < 0 || threshold > 100) {
        throw new IllegalArgumentException("Humidity threshold must be between 0 and 100 percent");
      }
    }
    this.threshold = threshold;
  }

  public List<String> getPrecipitationConditions() {
    return precipitationConditions != null ? new ArrayList<>(precipitationConditions) : null;
  }

  public void setPriority(int priority) {
    if (priority < 1 || priority > 5) {
      throw new IllegalArgumentException("Priority must be between 1 (highest) and 5 (lowest)");
    }
    this.priority = priority;
  }

  /**
   * Triggers the alert and updates the last triggered time
   */
  public void trigger() {
    this.lastTriggered = LocalDateTime.now();
  }

  /**
   * Deactivates the alert
   */
  public void deactivate() {
    this.active = false;
  }

  /**
   * Checks if this alert has been triggered recently (within last hour)
   *
   * @return true if recently triggered
   */
  public boolean wasRecentlyTriggered() {
    if (lastTriggered == null) {
      return false;
    }
    return lastTriggered.isAfter(LocalDateTime.now().minusHours(1));
  }

  /**
   * Gets a human-readable description of the alert
   *
   * @return alert description
   */
  public String getDescription() {
    StringBuilder desc = new StringBuilder();
    desc.append(alertType.getDisplayName()).append(" alert for ").append(location.getFullName());

    if (alertType == AlertType.HIGH_TEMPERATURE) {
      desc.append(" when temperature exceeds ").append(threshold).append("°C");
    } else if (alertType == AlertType.LOW_TEMPERATURE) {
      desc.append(" when temperature drops below ").append(threshold).append("°C");
    } else if (alertType == AlertType.HIGH_WIND_SPEED) {
      desc.append(" when wind speed exceeds ").append(threshold).append(" km/h");
    } else if (alertType == AlertType.PRECIPITATION) {
      if (precipitationConditions != null) {
        desc.append(" for conditions: ").append(String.join(",", precipitationConditions));
      }
    }

    return desc.toString();
  }
}

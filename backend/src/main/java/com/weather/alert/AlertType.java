package com.weather.alert;

import lombok.Getter;

/**
 * Enumeration of different types of weather alerts
 */
@Getter
public enum AlertType {

  HIGH_TEMPERATURE("High Temperature", "Temperature exceeds threshold"),
  LOW_TEMPERATURE("Low Temperature", "Temperature below threshold"),
  PRECIPITATION("Precipitation", "Precipitation conditions detected"),
  HIGH_WIND_SPEED("High Wind Speed", "Wind speed exceeds threshold"),
  HIGH_HUMIDITY("High Humidity", "Humidity exceeds threshold"),
  LOW_HUMIDITY("Low Humidity", "Humidity below threshold");

  private final String displayName;
  private final String description;

  AlertType(String displayName, String description) {
    this.displayName = displayName;
    this.description = description;
  }

  @Override
  public String toString() {
    return displayName;
  }
}

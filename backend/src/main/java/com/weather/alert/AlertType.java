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
  LOW_HUMIDITY("Low Humidity", "Humidity below threshold"),
  SEVERE_WEATHER("Severe Weather", "Severe weather conditions detected"),
  UV_INDEX("High UV Index", "UV index exceeds safe levels"),
  AIR_QUALITY("Poor Air Quality", "Air quality below acceptable levels"),
  FROST("Frost Warning", "Frost conditions expected"),
  HEAT_WAVE("Heat Wave", "Extended period of high temperatures"),
  COLD_SNAP("Cold Snap", "Extended period of low temperatures");

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

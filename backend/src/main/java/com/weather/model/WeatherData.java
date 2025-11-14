package com.weather.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

import static java.time.ZoneId.*;

/**
 * Weather data model with validation and unit conversion capabilities.
 * Temperature is stored in Celsius, timestamp tracks data freshness.
 */
@Getter
@EqualsAndHashCode(exclude = "timestamp")
@ToString
public class WeatherData {

  private double temperature;
  private String condition;
  private int humidity;
  private double windSpeed;
  @Setter
  private LocalDateTime timestamp;

  private static final double ABSOLUTE_ZERO_CELSIUS = -273.15;
  private static final double MAX_RECORDED_TEMPERATURE = 100.0;

  public WeatherData() {
    this.timestamp = LocalDateTime.now();
  }

  public WeatherData(double temperature, String condition, int humidity, double windSpeed) {
    this();
    setTemperature(temperature);
    setCondition(condition);
    setHumidity(humidity);
    setWindSpeed(windSpeed);
  }

  /**
   * @param temperature in Celsius, must be above absolute zero (-273.15°C) and below 100°C
   */
  public void setTemperature(double temperature) {
    if (temperature < ABSOLUTE_ZERO_CELSIUS) {
      throw new IllegalArgumentException("Temperature cannot be below absolute zero (-273.15°C)");
    }
    if (temperature > MAX_RECORDED_TEMPERATURE) {
      throw new IllegalArgumentException("Temperature exceeds reasonable maximum (" + MAX_RECORDED_TEMPERATURE + "°C)");
    }
    this.temperature = temperature;
  }

  public void setCondition(String condition) {
    if (condition == null) {
      throw new IllegalArgumentException("Weather condition cannot be null");
    }
    if (condition.trim().isEmpty()) {
      throw new IllegalArgumentException("Weather condition cannot be empty");
    }
    this.condition = condition.trim();
  }

  /**
   * @param humidity percentage value between 0-100
   */
  public void setHumidity(int humidity) {
    if (humidity < 0 || humidity > 100) {
      throw new IllegalArgumentException("Humidity must be between 0 and 100 percent");
    }
    this.humidity = humidity;
  }

  public void setWindSpeed(double windSpeed) {
    if (windSpeed < 0) {
      throw new IllegalArgumentException("Wind speed cannot be negative");
    }
    this.windSpeed = windSpeed;
  }

  public double getTemperatureInFahrenheit() {
    return (temperature * 9.0 / 5.0) + 32.0;
  }

  public void setTemperatureFromFahrenheit(double fahrenheit) {
    double celsius = (fahrenheit - 32.0) * 5.0 / 9.0;
    setTemperature(celsius);
  }

  /**
   * @return true if data is less than 1 hour old
   */
  public boolean isFresh() {
    long oneHourInMillis = 60 * 60 * 1000;
    long timestampInMillis = timestamp.atZone(systemDefault()).toInstant().toEpochMilli();
    return (System.currentTimeMillis() - timestampInMillis) < oneHourInMillis;
  }

  public String getDescription() {
    return String.format("%.1f°C, %s, %d%% humidity, %.1f km/h wind",
            temperature, condition, humidity, windSpeed);
  }
}

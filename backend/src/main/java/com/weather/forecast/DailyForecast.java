package com.weather.forecast;

import com.weather.model.WeatherData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Represents a daily weather forecast
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"date", "weatherData"})
@ToString
public class DailyForecast {

  private LocalDate date;
  private WeatherData weatherData;
  private double minTemperature;
  private double maxTemperature;
  private String summary;

  public DailyForecast(LocalDate date, WeatherData weatherData) {
    setDate(date);
    if (weatherData != null) {
      setWeatherData(weatherData);
      // Initialize min/max with current temperature
      this.minTemperature = weatherData.getTemperature();
      this.maxTemperature = weatherData.getTemperature();
    }
  }

  public void setDate(LocalDate date) {
    if (date == null) {
      throw new IllegalArgumentException("Date cannot be null");
    }
    this.date = date;
  }

  public void setWeatherData(WeatherData weatherData) {
    if (weatherData == null) {
      throw new IllegalArgumentException("Weather data cannot be null");
    }
    this.weatherData = weatherData;
  }

  /**
   * Updates the temperature range with a new temperature value
   *
   * @param temperature new temperature to consider
   */
  public void updateTemperatureRange(double temperature) {
    this.minTemperature = Math.min(this.minTemperature, temperature);
    this.maxTemperature = Math.max(this.maxTemperature, temperature);
  }

  /**
   * Returns the temperature range as a formatted string
   *
   * @return temperature range string
   */
  public String getTemperatureRange() {
    return String.format("%.1f°C - %.1f°C", minTemperature, maxTemperature);
  }
}

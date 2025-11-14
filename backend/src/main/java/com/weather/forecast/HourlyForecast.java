package com.weather.forecast;

import com.weather.model.WeatherData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Represents an hourly weather forecast
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"dateTime", "weatherData"})
@ToString
public class HourlyForecast {

  private LocalDateTime dateTime;
  private WeatherData weatherData;
  private double precipitationProbability;
  private double uvIndex;

  public HourlyForecast(LocalDateTime dateTime, WeatherData weatherData) {
    setDateTime(dateTime);
    setWeatherData(weatherData);
  }

  public void setDateTime(LocalDateTime dateTime) {
    if (dateTime == null) {
      throw new IllegalArgumentException("DateTime cannot be null");
    }
    this.dateTime = dateTime;
  }

  public void setWeatherData(WeatherData weatherData) {
    if (weatherData == null) {
      throw new IllegalArgumentException("Weather data cannot be null");
    }
    this.weatherData = weatherData;
  }

  public void setPrecipitationProbability(double precipitationProbability) {
    if (precipitationProbability < 0 || precipitationProbability > 100) {
      throw new IllegalArgumentException("Precipitation probability must be between 0 and 100");
    }
    this.precipitationProbability = precipitationProbability;
  }

  public void setUvIndex(double uvIndex) {
    if (uvIndex < 0) {
      throw new IllegalArgumentException("UV index cannot be negative");
    }
    this.uvIndex = uvIndex;
  }

  /**
   * Returns the hour of the day (0-23)
   *
   * @return hour of day
   */
  public int getHourOfDay() {
    return dateTime.getHour();
  }

  /**
   * Checks if this forecast is for daytime hours (6 AM - 6 PM)
   *
   * @return true if daytime
   */
  public boolean isDaytime() {
    int hour = getHourOfDay();
    return hour >= 6 && hour < 18;
  }

  /**
   * Returns a short time string (e.g., "14:00")
   *
   * @return formatted time string
   */
  public String getTimeString() {
    return String.format("%02d:%02d", dateTime.getHour(), dateTime.getMinute());
  }
}

package com.weather.forecast;

import com.weather.model.Location;
import com.weather.model.WeatherData;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a complete weather forecast for a location
 */
@ToString
public class WeatherForecast {

  private static final int MAX_FORECAST_DAYS = 14;
  @Getter
  private final Location location;
  private final Map<LocalDate, DailyForecast> dailyForecasts;
  private final List<HourlyForecast> hourlyForecasts;
  @Getter
  @Setter
  private LocalDateTime lastUpdated;

  public WeatherForecast(Location location) {
    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null");
    }
    this.location = location;
    this.dailyForecasts = new LinkedHashMap<>();
    this.hourlyForecasts = new ArrayList<>();
    this.lastUpdated = LocalDateTime.now();
  }


  /**
   * Adds a daily forecast for a specific date
   *
   * @param date        the date
   * @param weatherData the weather data
   */
  public void addDailyForecast(LocalDate date, WeatherData weatherData) {
    if (date == null) {
      throw new IllegalArgumentException("Date cannot be null");
    }
    if (weatherData == null) {
      throw new IllegalArgumentException("Weather data cannot be null");
    }
    if (date.isBefore(LocalDate.now())) {
      throw new IllegalArgumentException("Cannot add forecast for past dates");
    }
    if (date.isAfter(LocalDate.now().plusDays(MAX_FORECAST_DAYS))) {
      throw new IllegalArgumentException("Forecast period cannot exceed " + MAX_FORECAST_DAYS + " days");
    }

    DailyForecast dailyForecast = new DailyForecast(date, weatherData);
    dailyForecasts.put(date, dailyForecast);
    this.lastUpdated = LocalDateTime.now();
  }

  /**
   * Adds an hourly forecast for a specific datetime
   *
   * @param dateTime    the datetime
   * @param weatherData the weather data
   */
  public void addHourlyForecast(LocalDateTime dateTime, WeatherData weatherData) {
    if (dateTime == null) {
      throw new IllegalArgumentException("DateTime cannot be null");
    }
    if (weatherData == null) {
      throw new IllegalArgumentException("Weather data cannot be null");
    }
    if (dateTime.isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("Cannot add forecast for past times");
    }

    HourlyForecast hourlyForecast = new HourlyForecast(dateTime, weatherData);
    hourlyForecasts.add(hourlyForecast);

    hourlyForecasts.sort(Comparator.comparing(HourlyForecast::getDateTime));
    this.lastUpdated = LocalDateTime.now();
  }

  /**
   * Gets the daily forecast for a specific date
   *
   * @param date the date
   * @return daily forecast or null if not found
   */
  public DailyForecast getDailyForecast(LocalDate date) {
    return dailyForecasts.get(date);
  }

  /**
   * Gets all daily forecasts sorted by date
   *
   * @return list of daily forecasts
   */
  public List<DailyForecast> getDailyForecasts() {
    return dailyForecasts.values().stream()
            .sorted(Comparator.comparing(DailyForecast::getDate))
            .collect(Collectors.toList());
  }

  /**
   * Gets all hourly forecasts sorted by datetime
   *
   * @return list of hourly forecasts
   */
  public List<HourlyForecast> getHourlyForecasts() {
    return new ArrayList<>(hourlyForecasts);
  }

  /**
   * Calculates the average temperature across all daily forecasts
   *
   * @return average temperature
   */
  public double getAverageTemperature() {
    return dailyForecasts.values().stream()
            .mapToDouble(forecast -> forecast.getWeatherData().getTemperature())
            .average()
            .orElse(0.0);
  }
}

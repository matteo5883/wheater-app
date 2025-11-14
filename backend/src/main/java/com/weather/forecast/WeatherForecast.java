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

  @Getter
  private final Location location;
  private final Map<LocalDate, DailyForecast> dailyForecasts;
  private final List<HourlyForecast> hourlyForecasts;
  @Getter
  @Setter
  private LocalDateTime lastUpdated;

  private static final int MAX_FORECAST_DAYS = 14;

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
   * Gets hourly forecasts for a specific date
   *
   * @param date the date
   * @return list of hourly forecasts for that date
   */
  public List<HourlyForecast> getHourlyForecastsForDate(LocalDate date) {
    return hourlyForecasts.stream()
            .filter(forecast -> forecast.getDateTime().toLocalDate().equals(date))
            .collect(Collectors.toList());
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

  /**
   * Gets the minimum temperature from all forecasts
   *
   * @return minimum temperature
   */
  public double getMinTemperature() {
    return dailyForecasts.values().stream()
            .mapToDouble(forecast -> forecast.getWeatherData().getTemperature())
            .min()
            .orElse(Double.MAX_VALUE);
  }

  /**
   * Gets the maximum temperature from all forecasts
   *
   * @return maximum temperature
   */
  public double getMaxTemperature() {
    return dailyForecasts.values().stream()
            .mapToDouble(forecast -> forecast.getWeatherData().getTemperature())
            .max()
            .orElse(Double.MIN_VALUE);
  }

  /**
   * Checks if there are any precipitation alerts in the forecast
   *
   * @return true if precipitation is expected
   */
  public boolean hasPrecipitationAlert() {
    return dailyForecasts.values().stream()
            .anyMatch(forecast -> {
              String condition = forecast.getWeatherData().getCondition().toLowerCase();
              return condition.contains("rain") || condition.contains("snow") ||
                      condition.contains("storm") || condition.contains("drizzle");
            });
  }

  /**
   * Gets the forecast summary for the next few days
   *
   * @param days number of days to include in summary
   * @return forecast summary
   */
  public String getForecastSummary(int days) {
    if (days <= 0) {
      throw new IllegalArgumentException("Number of days must be positive");
    }

    List<DailyForecast> forecasts = getDailyForecasts().stream()
            .limit(days)
            .collect(Collectors.toList());

    if (forecasts.isEmpty()) {
      return "No forecast data available";
    }

    StringBuilder summary = new StringBuilder();
    summary.append(String.format("Forecast for %s:\n", location.getFullName()));

    for (DailyForecast forecast : forecasts) {
      summary.append(String.format("%s: %.1f°C, %s\n",
              forecast.getDate().toString(),
              forecast.getWeatherData().getTemperature(),
              forecast.getWeatherData().getCondition()));
    }

    return summary.toString().trim();
  }

  /**
   * Clears all forecast data
   */
  public void clearForecasts() {
    dailyForecasts.clear();
    hourlyForecasts.clear();
    this.lastUpdated = LocalDateTime.now();
  }

  /**
   * Gets the number of days for which we have forecasts
   *
   * @return number of forecast days
   */
  public int getForecastDays() {
    return dailyForecasts.size();
  }

  /**
   * Checks if the forecast data is stale (older than 1 hour)
   *
   * @return true if data needs refresh
   */
  public boolean isStale() {
    return lastUpdated.isBefore(LocalDateTime.now().minusHours(1));
  }
}

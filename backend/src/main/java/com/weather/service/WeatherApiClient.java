package com.weather.service;

import com.weather.model.Location;
import com.weather.model.WeatherData;

/**
 * Interface for weather API client implementations
 */
public interface WeatherApiClient {

  /**
   * Fetches current weather data for a location
   *
   * @param location the location to get weather for
   * @return current weather data
   */
  WeatherData getCurrentWeather(Location location);

  /**
   * Fetches weather forecast for multiple days
   *
   * @param location the location to get forecast for
   * @param days     number of days to forecast
   * @return array of weather data for each day
   */
  WeatherData[] getWeatherForecast(Location location, int days);
}

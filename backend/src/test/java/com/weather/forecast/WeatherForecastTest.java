package com.weather.forecast;

import com.weather.model.Location;
import com.weather.model.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Weather Forecast Tests")
public class WeatherForecastTest {

  private WeatherForecast weatherForecast;
  private Location testLocation;

  @BeforeEach
  void setUp() {
    testLocation = new Location("Milan", "IT", 45.4642, 9.1900);
    weatherForecast = new WeatherForecast(testLocation);
  }

  @Test
  @DisplayName("Should create forecast for valid location")
  void shouldCreateForecastForValidLocation() {
    // When & Then
    assertNotNull(weatherForecast);
    assertEquals(testLocation, weatherForecast.getLocation());
    assertNotNull(weatherForecast.getDailyForecasts());
    assertNotNull(weatherForecast.getHourlyForecasts());
  }

  @Test
  @DisplayName("Should throw exception for null location")
  void shouldThrowExceptionForNullLocation() {
    // When & Then
    assertThrows(IllegalArgumentException.class,
            () -> new WeatherForecast(null));
  }

  @Test
  @DisplayName("Should add daily forecast correctly")
  void shouldAddDailyForecastCorrectly() {
    // Given
    LocalDate date = LocalDate.now().plusDays(1);
    WeatherData dailyWeather = createSampleWeatherData();

    // When
    weatherForecast.addDailyForecast(date, dailyWeather);

    // Then
    List<DailyForecast> dailyForecasts = weatherForecast.getDailyForecasts();
    assertEquals(1, dailyForecasts.size());
    assertEquals(date, dailyForecasts.get(0).getDate());
    assertEquals(dailyWeather, dailyForecasts.get(0).getWeatherData());
  }

  @Test
  @DisplayName("Should add hourly forecast correctly")
  void shouldAddHourlyForecastCorrectly() {
    // Given
    LocalDateTime dateTime = LocalDateTime.now().plusHours(3);
    WeatherData hourlyWeather = createSampleWeatherData();

    // When
    weatherForecast.addHourlyForecast(dateTime, hourlyWeather);

    // Then
    List<HourlyForecast> hourlyForecasts = weatherForecast.getHourlyForecasts();
    assertEquals(1, hourlyForecasts.size());
    assertEquals(dateTime, hourlyForecasts.get(0).getDateTime());
    assertEquals(hourlyWeather, hourlyForecasts.get(0).getWeatherData());
  }

  @Test
  @DisplayName("Should not add duplicate daily forecasts for same date")
  void shouldNotAddDuplicateDailyForecastsForSameDate() {
    // Given
    LocalDate date = LocalDate.now().plusDays(1);
    WeatherData weather1 = createSampleWeatherData();
    WeatherData weather2 = createSampleWeatherData();
    weather2.setTemperature(30.0);

    // When
    weatherForecast.addDailyForecast(date, weather1);
    weatherForecast.addDailyForecast(date, weather2); // Should replace the first one

    // Then
    List<DailyForecast> dailyForecasts = weatherForecast.getDailyForecasts();
    assertEquals(1, dailyForecasts.size());
    assertEquals(30.0, dailyForecasts.get(0).getWeatherData().getTemperature());
  }

  @Test
  @DisplayName("Should throw exception for past date in daily forecast")
  void shouldThrowExceptionForPastDateInDailyForecast() {
    // Given
    LocalDate pastDate = LocalDate.now().minusDays(1);
    WeatherData weather = createSampleWeatherData();

    // When & Then
    assertThrows(IllegalArgumentException.class,
            () -> weatherForecast.addDailyForecast(pastDate, weather));
  }

  @Test
  @DisplayName("Should throw exception for past datetime in hourly forecast")
  void shouldThrowExceptionForPastDatetimeInHourlyForecast() {
    // Given
    LocalDateTime pastDateTime = LocalDateTime.now().minusHours(1);
    WeatherData weather = createSampleWeatherData();

    // When & Then
    assertThrows(IllegalArgumentException.class,
            () -> weatherForecast.addHourlyForecast(pastDateTime, weather));
  }

  @Test
  @DisplayName("Should get forecast for specific date")
  void shouldGetForecastForSpecificDate() {
    // Given
    LocalDate targetDate = LocalDate.now().plusDays(2);
    WeatherData expectedWeather = createSampleWeatherData();
    weatherForecast.addDailyForecast(targetDate, expectedWeather);

    // When
    DailyForecast forecast = weatherForecast.getDailyForecast(targetDate);

    // Then
    assertNotNull(forecast);
    assertEquals(targetDate, forecast.getDate());
    assertEquals(expectedWeather, forecast.getWeatherData());
  }

  @Test
  @DisplayName("Should return null for non-existent date forecast")
  void shouldReturnNullForNonExistentDateForecast() {
    // Given
    LocalDate nonExistentDate = LocalDate.now().plusDays(10);

    // When
    DailyForecast forecast = weatherForecast.getDailyForecast(nonExistentDate);

    // Then
    assertNull(forecast);
  }

  @Test
  @DisplayName("Should sort daily forecasts by date")
  void shouldSortDailyForecastsByDate() {
    // Given
    LocalDate date1 = LocalDate.now().plusDays(3);
    LocalDate date2 = LocalDate.now().plusDays(1);
    LocalDate date3 = LocalDate.now().plusDays(2);

    WeatherData weather = createSampleWeatherData();

    // When
    weatherForecast.addDailyForecast(date1, weather);
    weatherForecast.addDailyForecast(date2, weather);
    weatherForecast.addDailyForecast(date3, weather);

    // Then
    List<DailyForecast> forecasts = weatherForecast.getDailyForecasts();
    assertEquals(3, forecasts.size());
    assertTrue(forecasts.get(0).getDate().isBefore(forecasts.get(1).getDate()));
    assertTrue(forecasts.get(1).getDate().isBefore(forecasts.get(2).getDate()));
  }

  @Test
  @DisplayName("Should sort hourly forecasts by datetime")
  void shouldSortHourlyForecastsByDatetime() {
    // Given
    LocalDateTime time1 = LocalDateTime.now().plusHours(6);
    LocalDateTime time2 = LocalDateTime.now().plusHours(2);
    LocalDateTime time3 = LocalDateTime.now().plusHours(4);

    WeatherData weather = createSampleWeatherData();

    // When
    weatherForecast.addHourlyForecast(time1, weather);
    weatherForecast.addHourlyForecast(time2, weather);
    weatherForecast.addHourlyForecast(time3, weather);

    // Then
    List<HourlyForecast> forecasts = weatherForecast.getHourlyForecasts();
    assertEquals(3, forecasts.size());
    assertTrue(forecasts.get(0).getDateTime().isBefore(forecasts.get(1).getDateTime()));
    assertTrue(forecasts.get(1).getDateTime().isBefore(forecasts.get(2).getDateTime()));
  }

  @Test
  @DisplayName("Should limit forecast period to maximum days")
  void shouldLimitForecastPeriodToMaximumDays() {
    LocalDate farFutureDate = LocalDate.now().plusDays(20);
    WeatherData weather = createSampleWeatherData();

    assertThrows(IllegalArgumentException.class,
            () -> weatherForecast.addDailyForecast(farFutureDate, weather));
  }

  @Test
  @DisplayName("Should calculate average temperature for forecast period")
  void shouldCalculateAverageTemperatureForForecastPeriod() {
    weatherForecast.addDailyForecast(LocalDate.now().plusDays(1),
            createWeatherDataWithTemperature(20.0));
    weatherForecast.addDailyForecast(LocalDate.now().plusDays(2),
            createWeatherDataWithTemperature(25.0));
    weatherForecast.addDailyForecast(LocalDate.now().plusDays(3),
            createWeatherDataWithTemperature(30.0));

    double averageTemp = weatherForecast.getAverageTemperature();

    assertEquals(25.0, averageTemp, 0.1);
  }

  private WeatherData createSampleWeatherData() {
    WeatherData weatherData = new WeatherData();
    weatherData.setTemperature(22.5);
    weatherData.setCondition("Partly Cloudy");
    weatherData.setHumidity(65);
    weatherData.setWindSpeed(10.5);
    return weatherData;
  }

  private WeatherData createWeatherDataWithTemperature(double temperature) {
    WeatherData weatherData = createSampleWeatherData();
    weatherData.setTemperature(temperature);
    return weatherData;
  }
}

package com.weather.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.config.WeatherApiProperties;
import com.weather.model.Location;
import com.weather.model.WeatherData;
import com.weather.service.NetworkException;
import com.weather.service.WeatherApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OpenWeatherMap API implementation of WeatherApiClient
 * All dependencies are injected by Spring via constructor
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenWeatherMapApiClient implements WeatherApiClient {

  private final WeatherApiProperties weatherProperties;
  private final ObjectMapper objectMapper;
  private final OkHttpClient httpClient;

  @Override
  public WeatherData getCurrentWeather(Location location) {
    String url = String.format("%s/weather?lat=%f&lon=%f&appid=%s&units=metric",
            weatherProperties.getBaseUrl(), location.getLatitude(), location.getLongitude(), weatherProperties.getApiKey());

    Request request = new Request.Builder()
            .url(url)
            .get()
            .build();

    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new RuntimeException("API call failed with code: " + response.code());
      }

      String responseBody = response.body().string();
      return parseCurrentWeather(responseBody);

    } catch (IOException e) {
      throw new NetworkException("Network error while fetching current weather", e);
    } catch (Exception e) {
      throw new RuntimeException("Error parsing weather data", e);
    }
  }

  @Override
  public WeatherData[] getWeatherForecast(Location location, int days) {
    String url = String.format("%s/forecast?lat=%f&lon=%f&appid=%s&units=metric&cnt=%d",
            weatherProperties.getBaseUrl(), location.getLatitude(), location.getLongitude(), weatherProperties.getApiKey(), days * 8); // 8 forecasts per day (3-hour intervals)

    Request request = new Request.Builder()
            .url(url)
            .get()
            .build();

    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new RuntimeException("API call failed with code: " + response.code());
      }

      assert response.body() != null;
      String responseBody = response.body().string();
      return parseForecastWeather(responseBody, days);

    } catch (IOException e) {
      throw new NetworkException("Network error while fetching forecast", e);
    } catch (Exception e) {
      throw new RuntimeException("Error parsing forecast data", e);
    }
  }

  private WeatherData parseCurrentWeather(String json) throws Exception {
    JsonNode root = objectMapper.readTree(json);

    WeatherData weatherData = new WeatherData();
    weatherData.setTemperature(root.path("main").path("temp").asDouble());
    weatherData.setHumidity(root.path("main").path("humidity").asInt());
    weatherData.setWindSpeed(root.path("wind").path("speed").asDouble() * 3.6); // Convert m/s to km/h

    JsonNode weatherArray = root.path("weather");
    if (weatherArray.isArray() && !weatherArray.isEmpty()) {
      weatherData.setCondition(weatherArray.get(0).path("main").asText());
    }

    return weatherData;
  }

  private WeatherData[] parseForecastWeather(String json, int days) throws Exception {
    JsonNode root = objectMapper.readTree(json);
    JsonNode listNode = root.path("list");

    WeatherData[] forecast = new WeatherData[days];

    // Group forecasts by day (taking the first forecast of each day)
    for (int i = 0; i < Math.min(days, listNode.size() / 8); i++) {
      JsonNode dayForecast = listNode.get(i * 8); // Take every 8th forecast (24 hours apart)

      WeatherData weatherData = new WeatherData();
      weatherData.setTemperature(dayForecast.path("main").path("temp").asDouble());
      weatherData.setHumidity(dayForecast.path("main").path("humidity").asInt());
      weatherData.setWindSpeed(dayForecast.path("wind").path("speed").asDouble() * 3.6);

      JsonNode weatherArray = dayForecast.path("weather");
      if (weatherArray.isArray() && !weatherArray.isEmpty()) {
        weatherData.setCondition(weatherArray.get(0).path("main").asText());
      }

      forecast[i] = weatherData;
    }

    return forecast;
  }
}

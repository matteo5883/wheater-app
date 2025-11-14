package com.weather.service.impl;

import com.weather.model.Location;
import com.weather.model.WeatherData;
import com.weather.service.NetworkException;
import com.weather.service.WeatherApiClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock implementation of WeatherApiClient for testing and development
 */
@Slf4j
public class MockWeatherApiClient implements WeatherApiClient {

    private final Random random = new Random();
    private final Map<String, WeatherData> mockResponses = new ConcurrentHashMap<>();
    private boolean simulateNetworkError = false;
    private boolean simulateApiError = false;

    public MockWeatherApiClient() {
        initializeMockData();
    }

    @Override
    public WeatherData getCurrentWeather(Location location) {
        if (simulateNetworkError) {
            throw new NetworkException("Simulated network error");
        }

        if (simulateApiError) {
            throw new RuntimeException("Simulated API error");
        }

        String key = generateLocationKey(location);
        WeatherData mockData = mockResponses.get(key);

        if (mockData != null) {
            return mockData;
        }

        // Generate realistic mock data
        return generateRealisticWeatherData(location);
    }

    @Override
    public WeatherData[] getWeatherForecast(Location location, int days) {
        if (simulateNetworkError) {
            throw new NetworkException("Simulated network error");
        }

        if (simulateApiError) {
            throw new RuntimeException("Simulated API error");
        }

        WeatherData[] forecast = new WeatherData[days];
        WeatherData currentWeather = getCurrentWeather(location);

        for (int i = 0; i < days; i++) {
            forecast[i] = generateForecastDay(currentWeather, i);
        }

        return forecast;
    }

    // Testing utilities
    public void setMockResponse(Location location, WeatherData weatherData) {
        mockResponses.put(generateLocationKey(location), weatherData);
    }

    public void setSimulateNetworkError(boolean simulate) {
        this.simulateNetworkError = simulate;
    }

    public void setSimulateApiError(boolean simulate) {
        this.simulateApiError = simulate;
    }

    public void clearMockResponses() {
        mockResponses.clear();
        initializeMockData();
    }

    private void initializeMockData() {
        // Pre-populate with some common test locations
        mockResponses.put("Milan_IT", createWeatherData(22.5, "Partly Cloudy", 65, 10.5));
        mockResponses.put("Rome_IT", createWeatherData(25.0, "Sunny", 55, 8.0));
        mockResponses.put("London_GB", createWeatherData(15.0, "Cloudy", 80, 15.0));
        mockResponses.put("New York_US", createWeatherData(20.0, "Clear", 60, 12.0));
    }

    private String generateLocationKey(Location location) {
        return location.getCity().replaceAll("\\s+", " ") + "_" + location.getCountry();
    }

    private WeatherData createWeatherData(double temp, String condition, int humidity, double windSpeed) {
        WeatherData data = new WeatherData();
        data.setTemperature(temp);
        data.setCondition(condition);
        data.setHumidity(humidity);
        data.setWindSpeed(windSpeed);
        return data;
    }

    private WeatherData generateRealisticWeatherData(Location location) {
        // Generate realistic weather based on location (simplified)
        double baseTemp = calculateBaseTemperature(location);
        double temperature = baseTemp + (random.nextGaussian() * 5); // ±5°C variation

        String[] conditions = {"Sunny", "Partly Cloudy", "Cloudy", "Rainy", "Clear"};
        String condition = conditions[random.nextInt(conditions.length)];

        int humidity = 40 + random.nextInt(40); // 40-80%
        double windSpeed = 5 + (random.nextDouble() * 20); // 5-25 km/h

        return createWeatherData(temperature, condition, humidity, windSpeed);
    }

    private double calculateBaseTemperature(Location location) {
        // Simplified temperature model based on latitude
        double latitude = Math.abs(location.getLatitude());

        if (latitude < 23.5) { // Tropics
            return 25 + random.nextInt(10);
        } else if (latitude < 66.5) { // Temperate
            return 15 + random.nextInt(15);
        } else { // Polar
            return -5 + random.nextInt(15);
        }
    }

    private WeatherData generateForecastDay(WeatherData baseWeather, int dayOffset) {
        // Generate forecast with slight variations from current weather
        double tempVariation = (random.nextGaussian() * 3) + (dayOffset * 0.5); // Slight trend
        double newTemp = baseWeather.getTemperature() + tempVariation;

        int humidityVariation = random.nextInt(20) - 10; // ±10%
        int newHumidity = Math.max(0, Math.min(100, baseWeather.getHumidity() + humidityVariation));

        double windVariation = (random.nextGaussian() * 2);
        double newWindSpeed = Math.max(0, baseWeather.getWindSpeed() + windVariation);

        String[] forecastConditions = {"Sunny", "Partly Cloudy", "Cloudy", "Rainy", "Clear", "Thunderstorm"};
        String condition = forecastConditions[random.nextInt(forecastConditions.length)];

        return createWeatherData(newTemp, condition, newHumidity, newWindSpeed);
    }
}

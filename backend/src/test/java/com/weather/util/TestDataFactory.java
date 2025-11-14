package com.weather.util;

import com.weather.model.WeatherData;
import com.weather.model.Location;
import lombok.experimental.UtilityClass;

/**
 * Test utility class providing common test data and helper methods
 */
@UtilityClass
public class TestDataFactory {

    // Standard test locations
    public static final Location MILAN = new Location("Milan", "IT", 45.4642, 9.1900);
    public static final Location ROME = new Location("Rome", "IT", 41.9028, 12.4964);
    public static final Location FLORENCE = new Location("Florence", "IT", 43.7696, 11.2558);
    public static final Location VENICE = new Location("Venice", "IT", 45.4408, 12.3155);

    /**
     * Creates a sample WeatherData object with typical values
     */
    public static WeatherData createSampleWeatherData() {
        WeatherData weatherData = new WeatherData();
        weatherData.setTemperature(22.5);
        weatherData.setCondition("Partly Cloudy");
        weatherData.setHumidity(65);
        weatherData.setWindSpeed(10.5);
        return weatherData;
    }

    /**
     * Creates a WeatherData object with specified temperature
     */
    public static WeatherData createWeatherDataWithTemperature(double temperature) {
        WeatherData weatherData = createSampleWeatherData();
        weatherData.setTemperature(temperature);
        return weatherData;
    }

    /**
     * Creates a WeatherData object with specified condition
     */
    public static WeatherData createWeatherDataWithCondition(String condition) {
        WeatherData weatherData = createSampleWeatherData();
        weatherData.setCondition(condition);
        return weatherData;
    }

    /**
     * Creates a WeatherData object with specified wind speed
     */
    public static WeatherData createWeatherDataWithWindSpeed(double windSpeed) {
        WeatherData weatherData = createSampleWeatherData();
        weatherData.setWindSpeed(windSpeed);
        return weatherData;
    }

    /**
     * Creates a WeatherData object with specified humidity
     */
    public static WeatherData createWeatherDataWithHumidity(int humidity) {
        WeatherData weatherData = createSampleWeatherData();
        weatherData.setHumidity(humidity);
        return weatherData;
    }

    /**
     * Creates a WeatherData object representing hot weather
     */
    public static WeatherData createHotWeatherData() {
        WeatherData weatherData = new WeatherData();
        weatherData.setTemperature(38.0);
        weatherData.setCondition("Sunny");
        weatherData.setHumidity(30);
        weatherData.setWindSpeed(5.0);
        return weatherData;
    }

    /**
     * Creates a WeatherData object representing cold weather
     */
    public static WeatherData createColdWeatherData() {
        WeatherData weatherData = new WeatherData();
        weatherData.setTemperature(-5.0);
        weatherData.setCondition("Snow");
        weatherData.setHumidity(80);
        weatherData.setWindSpeed(20.0);
        return weatherData;
    }

    /**
     * Creates a WeatherData object representing rainy weather
     */
    public static WeatherData createRainyWeatherData() {
        WeatherData weatherData = new WeatherData();
        weatherData.setTemperature(18.0);
        weatherData.setCondition("Heavy Rain");
        weatherData.setHumidity(90);
        weatherData.setWindSpeed(15.0);
        return weatherData;
    }

    /**
     * Creates a WeatherData object representing stormy weather
     */
    public static WeatherData createStormyWeatherData() {
        WeatherData weatherData = new WeatherData();
        weatherData.setTemperature(25.0);
        weatherData.setCondition("Thunderstorm");
        weatherData.setHumidity(85);
        weatherData.setWindSpeed(45.0);
        return weatherData;
    }

    /**
     * Creates a location with random coordinates (for testing)
     */
    public static Location createRandomLocation(String city, String country) {
        double latitude = -90 + Math.random() * 180;  // -90 to 90
        double longitude = -180 + Math.random() * 360; // -180 to 180
        return new Location(city, country, latitude, longitude);
    }
}

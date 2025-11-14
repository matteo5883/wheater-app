package com.weather.util;

import com.weather.model.WeatherData;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for weather data formatting and conversions
 */
@UtilityClass
public class WeatherUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static double celsiusToFahrenheit(double celsius) {
        return (celsius * 9.0 / 5.0) + 32.0;
    }

    public static double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32.0) * 5.0 / 9.0;
    }

    public static double msToKmh(double ms) {
        return ms * 3.6;
    }

    public static double kmhToMs(double kmh) {
        return kmh / 3.6;
    }

    public static double kmhToMph(double kmh) {
        return kmh * 0.621371;
    }

    public static String getTemperatureDescription(double celsius) {
        if (celsius < -10) return "Freezing";
        if (celsius < 0) return "Very Cold";
        if (celsius < 10) return "Cold";
        if (celsius < 20) return "Cool";
        if (celsius < 25) return "Comfortable";
        if (celsius < 30) return "Warm";
        if (celsius < 35) return "Hot";
        return "Very Hot";
    }

    public static String getHumidityDescription(int humidity) {
        if (humidity < 30) return "Dry";
        if (humidity < 60) return "Comfortable";
        if (humidity < 80) return "Humid";
        return "Very Humid";
    }

    public static String getWindDescription(double kmh) {
        if (kmh < 5) return "Calm";
        if (kmh < 15) return "Light Breeze";
        if (kmh < 25) return "Moderate Breeze";
        if (kmh < 40) return "Strong Breeze";
        if (kmh < 60) return "Windy";
        return "Very Windy";
    }

    public static String formatWeatherDetailed(WeatherData weather) {
        return String.format(
            "Temperature: %.1f°C (%.1f°F) - %s\n" +
            "Condition: %s\n" +
            "Humidity: %d%% - %s\n" +
            "Wind: %.1f km/h - %s",
            weather.getTemperature(),
            celsiusToFahrenheit(weather.getTemperature()),
            getTemperatureDescription(weather.getTemperature()),
            weather.getCondition(),
            weather.getHumidity(),
            getHumidityDescription(weather.getHumidity()),
            weather.getWindSpeed(),
            getWindDescription(weather.getWindSpeed())
        );
    }

    public static String formatWeatherCompact(WeatherData weather) {
        return String.format("%.1f°C, %s, %d%% humidity",
            weather.getTemperature(),
            weather.getCondition(),
            weather.getHumidity()
        );
    }

    public static boolean isPrecipitation(String condition) {
        if (condition == null) return false;

        String lower = condition.toLowerCase();
        List<String> precipitationKeywords = Arrays.asList(
            "rain", "rainy", "drizzle", "shower", "snow", "sleet", "hail", "storm", "thunderstorm"
        );

        return precipitationKeywords.stream().anyMatch(lower::contains);
    }

    public static double calculateHeatIndex(double temperatureCelsius, int humidity) {
        double tempF = celsiusToFahrenheit(temperatureCelsius);

        if (tempF < 80) {
            return temperatureCelsius; // Heat index only applies at higher temperatures
        }

        // Simplified heat index calculation
        double hi = -42.379 + 2.04901523 * tempF + 10.14333127 * humidity
                   - 0.22475541 * tempF * humidity - 6.83783e-3 * tempF * tempF
                   - 5.481717e-2 * humidity * humidity + 1.22874e-3 * tempF * tempF * humidity
                   + 8.5282e-4 * tempF * humidity * humidity - 1.99e-6 * tempF * tempF * humidity * humidity;

        return fahrenheitToCelsius(hi);
    }

    public static String formatCurrentDateTime() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    public static double calculateWindChill(double temperatureCelsius, double windSpeedKmh) {
        double tempF = celsiusToFahrenheit(temperatureCelsius);
        double windMph = kmhToMph(windSpeedKmh);

        if (tempF > 50 || windMph < 3) {
            return temperatureCelsius; // Wind chill only applies in cold, windy conditions
        }

        // Wind chill formula (US National Weather Service)
        double windChillF = 35.74 + 0.6215 * tempF - 35.75 * Math.pow(windMph, 0.16) + 0.4275 * tempF * Math.pow(windMph, 0.16);

        return fahrenheitToCelsius(windChillF);
    }

    public static String getClothingSuggestion(WeatherData weather) {
        double temp = weather.getTemperature();
        String condition = weather.getCondition();
        double windSpeed = weather.getWindSpeed();

        StringBuilder suggestion = new StringBuilder();

        if (temp < 0) {
            suggestion.append("Heavy winter coat, gloves, hat, warm boots");
        } else if (temp < 10) {
            suggestion.append("Warm jacket, long pants");
        } else if (temp < 20) {
            suggestion.append("Light jacket or sweater");
        } else if (temp < 30) {
            suggestion.append("Light clothing, t-shirt");
        } else {
            suggestion.append("Light, breathable clothing, sun protection");
        }

        if (isPrecipitation(condition)) {
            suggestion.append(", umbrella or rain jacket");
        }

        if (windSpeed > 20) {
            suggestion.append(", windproof layer");
        }

        return suggestion.toString();
    }
}

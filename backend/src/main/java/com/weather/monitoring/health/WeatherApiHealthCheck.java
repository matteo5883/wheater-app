package com.weather.monitoring.health;

import com.weather.model.Location;
import com.weather.service.WeatherApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Health check for Weather API connectivity
 */
@Slf4j
@RequiredArgsConstructor
public class WeatherApiHealthCheck implements HealthCheck {

    private final WeatherApiClient apiClient;
    private static final Location TEST_LOCATION = new Location("London", "GB", 51.5074, -0.1278);

    @Override
    public HealthStatus check() {
        long startTime = System.currentTimeMillis();

        try {
            // Test API connectivity with a simple call
            apiClient.getCurrentWeather(TEST_LOCATION);

            long responseTime = System.currentTimeMillis() - startTime;

            Map<String, Object> details = new HashMap<>();
            details.put("test_location", TEST_LOCATION.getFullName());
            details.put("response_time_ms", responseTime);

            HealthStatus status = HealthStatus.up("weather-api");
            status.setResponseTimeMs(responseTime);
            status.setDetails(details);

            // Check if response time is acceptable
            if (responseTime > 5000) {
                return HealthStatus.degraded("weather-api",
                    "API responding slowly: " + responseTime + "ms");
            }

            return status;

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;

            Map<String, Object> details = new HashMap<>();
            details.put("error", e.getMessage());
            details.put("error_type", e.getClass().getSimpleName());
            details.put("response_time_ms", responseTime);

            HealthStatus status = HealthStatus.down("weather-api", e.getMessage());
            status.setResponseTimeMs(responseTime);
            status.setDetails(details);

            log.error("Weather API health check failed", e);
            return status;
        }
    }

    @Override
    public String getName() {
        return "weather-api";
    }

    @Override
    public long getTimeoutMs() {
        return 10000; // 10 seconds for API calls
    }
}

package com.weather.api.controller;

import com.weather.monitoring.circuit.CircuitBreaker;
import com.weather.service.WeatherService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for monitoring and operational endpoints
 */
@RestController
@RequestMapping("/api/v1/monitoring")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MonitoringController implements HealthIndicator {

  private final WeatherService weatherService;
  private final CircuitBreaker circuitBreaker;
  private final MeterRegistry meterRegistry;
  private final CacheManager cacheManager;

  @Override
  public Health health() {
    try {
      boolean serviceHealthy = weatherService.isHealthy();
      boolean circuitHealthy = !circuitBreaker.getState().name().equals("OPEN");

      Health.Builder builder = serviceHealthy && circuitHealthy ?
              Health.up() : Health.down();

      return builder
              .withDetail("weather-service", serviceHealthy ? "UP" : "DOWN")
              .withDetail("circuit-breaker", circuitBreaker.getState().name())
              .withDetail("cache-stats", weatherService.getCacheStatistics())
              .build();

    } catch (Exception e) {
      return Health.down()
              .withDetail("error", e.getMessage())
              .build();
    }
  }

  /**
   * Get detailed system health
   */
  @GetMapping("/health/detailed")
  public ResponseEntity<?> getDetailedHealth() {
    try {
      Map<String, Object> health = Map.of(
              "service", Map.of(
                      "status", weatherService.isHealthy() ? "UP" : "DOWN",
                      "cache", weatherService.getCacheStatistics()
              ),
              "circuitBreaker", Map.of(
                      "state", circuitBreaker.getState().name(),
                      "stats", circuitBreaker.getStats()
              ),
              "jvm", Map.of(
                      "memory", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(),
                      "maxMemory", Runtime.getRuntime().maxMemory(),
                      "threads", Thread.activeCount()
              ),
              "timestamp", System.currentTimeMillis()
      );

      return ResponseEntity.ok(health);

    } catch (Exception e) {
      log.error("Error getting detailed health", e);
      return ResponseEntity.internalServerError().body(Map.of(
              "error", "Failed to get health information",
              "status", "error"
      ));
    }
  }

  /**
   * Get circuit breaker status
   */
  @GetMapping("/circuit-breaker")
  public ResponseEntity<?> getCircuitBreakerStatus() {
    try {
      var stats = circuitBreaker.getStats();
      return ResponseEntity.ok(Map.of(
              "state", stats.getState().name(),
              "failureCount", stats.getFailureCount(),
              "successCount", stats.getSuccessCount(),
              "totalRequests", stats.getTotalRequests(),
              "failureRate", stats.getFailureRate(),
              "lastFailureTime", stats.getLastFailureTime(),
              "status", "success"
      ));
    } catch (Exception e) {
      log.error("Error getting circuit breaker status", e);
      return ResponseEntity.internalServerError().body(Map.of(
              "error", "Failed to get circuit breaker status",
              "status", "error"
      ));
    }
  }

  /**
   * Get application metrics summary
   */
  @GetMapping("/metrics/summary")
  public ResponseEntity<?> getMetricsSummary() {
    try {
      Counter.builder("weather.api.calls")
              .description("Total API calls")
              .register(meterRegistry)
              .increment();

      Gauge.builder("weather.cache.count", cacheManager.getCacheNames()::size)
              .description("Number of configured caches")
              .register(meterRegistry);

      return ResponseEntity.ok(Map.of(
              "metrics", Map.of(
                      "totalAPICalls", meterRegistry.find("weather.api.calls").counter() != null ?
                              meterRegistry.find("weather.api.calls").counter().count() : 0,
                      "cacheCount", cacheManager.getCacheNames().size(),
                      "cacheNames", cacheManager.getCacheNames(),
                      "circuitBreakerState", circuitBreaker.getState().name()
              ),
              "links", Map.of(
                      "prometheus", "/actuator/prometheus",
                      "health", "/actuator/health",
                      "metrics", "/actuator/metrics"
              ),
              "status", "success"
      ));

    } catch (Exception e) {
      log.error("Error getting metrics summary", e);
      return ResponseEntity.internalServerError().body(Map.of(
              "error", "Failed to get metrics summary",
              "status", "error"
      ));
    }
  }

  /**
   * Get application info
   */
  @GetMapping("/info")
  public ResponseEntity<?> getApplicationInfo() {
    return ResponseEntity.ok(Map.of(
            "application", Map.of(
                    "name", "WeatherApp",
                    "version", "1.0.0",
                    "description", "Enterprise Weather Application with Spring Boot",
                    "framework", "Spring Boot 3.1.5"
            ),
            "features", Map.of(
                    "restApi", true,
                    "caching", true,
                    "circuitBreaker", true,
                    "monitoring", true,
                    "prometheus", true,
                    "healthChecks", true
            ),
            "endpoints", Map.of(
                    "weather", "/api/v1/weather/**",
                    "alerts", "/api/v1/alerts/**",
                    "forecast", "/api/v1/forecast/**",
                    "monitoring", "/api/v1/monitoring/**",
                    "actuator", "/actuator/**"
            ),
            "timestamp", System.currentTimeMillis(),
            "status", "success"
    ));
  }
}

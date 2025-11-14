package com.weather.monitoring.metrics;

import com.weather.monitoring.health.HealthCheckManager;
import com.weather.monitoring.health.HealthStatus;
import com.weather.monitoring.circuit.CircuitBreaker;
import org.springframework.cache.CacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Collects and exports weather application metrics for Prometheus
 */
@Slf4j
@RequiredArgsConstructor
public class WeatherMetricsCollector {

    private final MetricsCollector metricsCollector;
    private final HealthCheckManager healthCheckManager;
    private final CacheManager cacheManager;
    private final Map<String, CircuitBreaker> circuitBreakers;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public void start() {
        log.info("Starting weather metrics collection");

        scheduler.scheduleAtFixedRate(this::collectMetrics, 0, 15, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(this::collectHealthMetrics, 0, 30, TimeUnit.SECONDS);
    }

    public void stop() {
        log.info("Stopping weather metrics collection");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public String getMetrics() {
        collectMetrics();
        return metricsCollector.getPrometheusMetrics();
    }

    private void collectMetrics() {
        try {
            collectCacheMetrics();
            collectCircuitBreakerMetrics();
            collectSystemMetrics();
        } catch (Exception e) {
            log.error("Error collecting metrics", e);
        }
    }

  private void collectCacheMetrics() {
    if (cacheManager != null) {
      // Count active caches
      long cacheCount = cacheManager.getCacheNames().size();
      metricsCollector.setGauge("weather_cache_count", cacheCount);

      // Check cache accessibility
      int accessibleCaches = 0;
      for (String cacheName : cacheManager.getCacheNames()) {
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
          accessibleCaches++;
        }
      }

      metricsCollector.setGauge("weather_cache_accessible_count", accessibleCaches);

      log.debug("Collected cache metrics: total_caches={}, accessible={}",
          cacheCount, accessibleCaches);
    }
  }

    private void collectCircuitBreakerMetrics() {
        for (Map.Entry<String, CircuitBreaker> entry : circuitBreakers.entrySet()) {
            String name = entry.getKey();
            CircuitBreaker.CircuitBreakerStats stats = entry.getValue().getStats();

            metricsCollector.setGauge("weather_circuit_breaker_state",
                stateToNumeric(stats.getState()), "name", name);

            metricsCollector.setGauge("weather_circuit_breaker_failure_count",
                stats.getFailureCount(), "name", name);

            metricsCollector.setGauge("weather_circuit_breaker_success_count",
                stats.getSuccessCount(), "name", name);

            metricsCollector.setGauge("weather_circuit_breaker_failure_rate",
                stats.getFailureRate(), "name", name);

            metricsCollector.setGauge("weather_circuit_breaker_total_requests",
                stats.getTotalRequests(), "name", name);

            log.debug("Collected circuit breaker metrics for {}: state={}, failures={}, successes={}",
                name, stats.getState(), stats.getFailureCount(), stats.getSuccessCount());
        }
    }

    private void collectHealthMetrics() {
        try {
            HealthCheckManager.AggregatedHealthStatus health = healthCheckManager.checkAll();

            // Overall health
            metricsCollector.setGauge("weather_health_status",
                health.isHealthy() ? 1.0 : 0.0);

            metricsCollector.setGauge("weather_health_response_time_ms",
                health.getTotalResponseTimeMs());

            // Individual health checks
            for (Map.Entry<String, HealthStatus> entry : health.getIndividualResults().entrySet()) {
                String checkName = entry.getKey();
                HealthStatus status = entry.getValue();

                metricsCollector.setGauge("weather_health_check_status",
                    status.isHealthy() ? 1.0 : 0.0, "check", checkName);

                metricsCollector.setGauge("weather_health_check_response_time_ms",
                    status.getResponseTimeMs(), "check", checkName);
            }

            log.debug("Collected health metrics: overall={}, checks={}",
                health.isHealthy(), health.getIndividualResults().size());

        } catch (Exception e) {
            log.error("Error collecting health metrics", e);
            metricsCollector.setGauge("weather_health_status", 0.0);
        }
    }

    private void collectSystemMetrics() {
        // JVM metrics
        Runtime runtime = Runtime.getRuntime();

        metricsCollector.setGauge("jvm_memory_used_bytes",
            runtime.totalMemory() - runtime.freeMemory());

        metricsCollector.setGauge("jvm_memory_total_bytes", runtime.totalMemory());

        metricsCollector.setGauge("jvm_memory_max_bytes", runtime.maxMemory());

        metricsCollector.setGauge("jvm_threads_current",
            Thread.activeCount());

        // Application uptime (simplified)
        metricsCollector.setGauge("weather_app_uptime_seconds",
            System.currentTimeMillis() / 1000.0);
    }

    private double stateToNumeric(com.weather.monitoring.circuit.CircuitBreakerState state) {
      return switch (state) {
        case CLOSED -> 0.0;
        case HALF_OPEN -> 0.5;
        case OPEN -> 1.0;
        default -> -1.0;
      };
    }
}

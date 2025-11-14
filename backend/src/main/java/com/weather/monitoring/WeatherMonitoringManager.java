package com.weather.monitoring;

import com.weather.monitoring.circuit.CircuitBreaker;
import com.weather.monitoring.health.CacheHealthCheck;
import com.weather.monitoring.health.HealthCheckManager;
import com.weather.monitoring.health.SystemHealthCheck;
import com.weather.monitoring.health.WeatherApiHealthCheck;
import com.weather.monitoring.http.MonitoringHttpServer;
import com.weather.monitoring.metrics.MetricsCollector;
import com.weather.monitoring.metrics.WeatherMetricsCollector;
import com.weather.service.WeatherApiClient;
import com.weather.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Main monitoring orchestrator for the weather application
 */
@Slf4j
public class WeatherMonitoringManager {

    private final HealthCheckManager healthCheckManager;
    private final WeatherMetricsCollector weatherMetricsCollector;
    private final MonitoringHttpServer httpServer;
    private final Map<String, CircuitBreaker> circuitBreakers;

  public WeatherMonitoringManager(WeatherService weatherService,
                                WeatherApiClient apiClient,
                                CacheManager cacheManager,
                                int httpPort) throws IOException {

        this.circuitBreakers = new HashMap<>();

        // Initialize health check manager
        this.healthCheckManager = new HealthCheckManager();
        setupHealthChecks(apiClient, cacheManager);

        // Initialize metrics collector
        MetricsCollector metricsCollector = new MetricsCollector();
        this.weatherMetricsCollector = new WeatherMetricsCollector(
            metricsCollector, healthCheckManager, cacheManager, circuitBreakers);

        // Initialize HTTP server
        this.httpServer = new MonitoringHttpServer(httpPort, weatherMetricsCollector, healthCheckManager);

        log.info("Weather monitoring manager initialized on port {}", httpPort);
    }

    /**
     * Starts all monitoring components
     */
    public void start() {
        log.info("Starting weather monitoring...");

        try {
            // Start metrics collection
            weatherMetricsCollector.start();

            // Start HTTP server for endpoints
            httpServer.start();

            log.info("Weather monitoring started successfully");

            // Log available endpoints
            logEndpoints();

        } catch (Exception e) {
            log.error("Failed to start weather monitoring", e);
            throw new RuntimeException("Monitoring startup failed", e);
        }
    }

    /**
     * Stops all monitoring components
     */
    public void stop() {
        log.info("Stopping weather monitoring...");

        try {
            weatherMetricsCollector.stop();
            httpServer.stop();

            log.info("Weather monitoring stopped successfully");

        } catch (Exception e) {
            log.error("Error stopping weather monitoring", e);
        }
    }

    /**
     * Registers a circuit breaker for monitoring
     */
    public void registerCircuitBreaker(String name, CircuitBreaker circuitBreaker) {
        circuitBreakers.put(name, circuitBreaker);
        log.info("Registered circuit breaker: {}", name);
    }

    /**
     * Gets health check manager for external use
     */
    public HealthCheckManager getHealthCheckManager() {
        return healthCheckManager;
    }

    /**
     * Gets metrics collector for external use
     */
    public WeatherMetricsCollector getMetricsCollector() {
        return weatherMetricsCollector;
    }

    /**
     * Performs a complete health check
     */
    public HealthCheckManager.AggregatedHealthStatus checkHealth() {
        return healthCheckManager.checkAll();
    }

    /**
     * Gets current metrics in Prometheus format
     */
    public String getMetrics() {
        return weatherMetricsCollector.getMetrics();
    }

    private void setupHealthChecks(WeatherApiClient apiClient,
                                 CacheManager cacheManager) {
        // Register API health check
        WeatherApiHealthCheck apiHealthCheck = new WeatherApiHealthCheck(apiClient);
        healthCheckManager.registerHealthCheck(apiHealthCheck);

        // Register cache health check
        CacheHealthCheck cacheHealthCheck = new CacheHealthCheck(cacheManager);
        healthCheckManager.registerHealthCheck(cacheHealthCheck);

        // Register system health check
        SystemHealthCheck systemHealthCheck = new SystemHealthCheck();
        healthCheckManager.registerHealthCheck(systemHealthCheck);

        log.info("Registered {} health checks", healthCheckManager.getHealthCheckNames().size());
    }

    private void logEndpoints() {
        log.info("Monitoring endpoints available:");
        log.info("  • http://localhost:8080/metrics - Prometheus metrics");
        log.info("  • http://localhost:8080/health - Detailed health status");
        log.info("  • http://localhost:8080/ready - Readiness probe (K8s)");
        log.info("  • http://localhost:8080/live - Liveness probe (K8s)");
        log.info("  • http://localhost:8080/info - Application information");
    }

    /**
     * Creates a monitoring manager with default configuration
     */
    public static WeatherMonitoringManager createDefault(
        WeatherService weatherService,
        WeatherApiClient apiClient,
        CacheManager cacheManager) {
      try {
        return new WeatherMonitoringManager(
            weatherService, apiClient, cacheManager, 8080);
      } catch (IOException e) {
        throw new RuntimeException("Failed to create monitoring manager", e);
      }
    }
}

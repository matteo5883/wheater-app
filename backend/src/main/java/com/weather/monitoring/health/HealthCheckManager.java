package com.weather.monitoring.health;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Aggregates and manages multiple health checks
 */
@Slf4j
public class HealthCheckManager {

    private final Map<String, HealthCheck> healthChecks = new ConcurrentHashMap<>();
    private final Map<String, HealthStatus> lastResults = new ConcurrentHashMap<>();

    public void registerHealthCheck(HealthCheck healthCheck) {
        healthChecks.put(healthCheck.getName(), healthCheck);
        log.info("Registered health check: {}", healthCheck.getName());
    }

    public void unregisterHealthCheck(String name) {
        healthChecks.remove(name);
        lastResults.remove(name);
        log.info("Unregistered health check: {}", name);
    }

    /**
     * Runs all health checks and returns aggregated status
     */
    public AggregatedHealthStatus checkAll() {
        Map<String, CompletableFuture<HealthStatus>> futures = new HashMap<>();

        // Execute all health checks asynchronously
        for (HealthCheck healthCheck : healthChecks.values()) {
            CompletableFuture<HealthStatus> future = CompletableFuture
                .supplyAsync(() -> executeHealthCheck(healthCheck))
                .orTimeout(healthCheck.getTimeoutMs(), TimeUnit.MILLISECONDS)
                .exceptionally(throwable -> {
                    log.error("Health check {} failed with timeout or error",
                        healthCheck.getName(), throwable);
                    return HealthStatus.down(healthCheck.getName(),
                        "Timeout or error: " + throwable.getMessage());
                });

            futures.put(healthCheck.getName(), future);
        }

        // Collect all results
        Map<String, HealthStatus> results = new HashMap<>();
        for (Map.Entry<String, CompletableFuture<HealthStatus>> entry : futures.entrySet()) {
            try {
                HealthStatus result = entry.getValue().get(10, TimeUnit.SECONDS);
                results.put(entry.getKey(), result);
                lastResults.put(entry.getKey(), result);
            } catch (Exception e) {
                log.error("Failed to get health check result for {}", entry.getKey(), e);
                HealthStatus failedResult = HealthStatus.down(entry.getKey(),
                    "Health check execution failed: " + e.getMessage());
                results.put(entry.getKey(), failedResult);
                lastResults.put(entry.getKey(), failedResult);
            }
        }

        return aggregateResults(results);
    }

    /**
     * Gets the last known status for a specific health check
     */
    public HealthStatus getHealthCheck(String name) {
        return lastResults.get(name);
    }

    /**
     * Gets all registered health check names
     */
    public Set<String> getHealthCheckNames() {
        return Collections.unmodifiableSet(healthChecks.keySet());
    }

    private HealthStatus executeHealthCheck(HealthCheck healthCheck) {
        try {
            log.debug("Executing health check: {}", healthCheck.getName());
            return healthCheck.check();
        } catch (Exception e) {
            log.error("Health check {} threw exception", healthCheck.getName(), e);
            return HealthStatus.down(healthCheck.getName(),
                "Health check exception: " + e.getMessage());
        }
    }

    private AggregatedHealthStatus aggregateResults(Map<String, HealthStatus> results) {
        boolean allHealthy = results.values().stream().allMatch(HealthStatus::isHealthy);
        long totalResponseTime = results.values().stream()
            .mapToLong(HealthStatus::getResponseTimeMs).sum();

        String overallStatus;
        if (allHealthy) {
            // Check if any are degraded
            boolean anyDegraded = results.values().stream()
                .anyMatch(status -> "DEGRADED".equals(status.getStatus()));
            overallStatus = anyDegraded ? "DEGRADED" : "UP";
        } else {
            overallStatus = "DOWN";
        }

        return AggregatedHealthStatus.builder()
            .healthy(allHealthy)
            .status(overallStatus)
            .totalResponseTimeMs(totalResponseTime)
            .individualResults(results)
            .timestamp(new Date())
            .build();
    }

    /**
     * Aggregated health status containing all individual results
     */
    @lombok.Data
    @lombok.Builder
    public static class AggregatedHealthStatus {
        private boolean healthy;
        private String status;
        private long totalResponseTimeMs;
        private Map<String, HealthStatus> individualResults;
        private Date timestamp;

        public List<HealthStatus> getFailedChecks() {
            return individualResults.values().stream()
                .filter(status -> !status.isHealthy())
                .collect(Collectors.toList());
        }

        public List<HealthStatus> getDegradedChecks() {
            return individualResults.values().stream()
                .filter(status -> "DEGRADED".equals(status.getStatus()))
                .collect(Collectors.toList());
        }
    }
}

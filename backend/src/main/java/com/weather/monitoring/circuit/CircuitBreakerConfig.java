package com.weather.monitoring.circuit;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;

/**
 * Configuration for circuit breaker behavior
 */
@Data
@Builder
public class CircuitBreakerConfig {

    @Builder.Default
    private int failureThreshold = 5; // Number of failures to open circuit

    @Builder.Default
    private int successThreshold = 3; // Number of successes to close circuit from half-open

    @Builder.Default
    private Duration timeout = Duration.ofMinutes(1); // Time to wait before trying half-open

    @Builder.Default
    private int windowSize = 10; // Size of the rolling window for failure tracking

    @Builder.Default
    private Duration callTimeout = Duration.ofSeconds(10); // Timeout for individual calls

    @Builder.Default
    private String name = "circuit-breaker"; // Name for logging and metrics
}

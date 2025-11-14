package com.weather.monitoring.circuit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Circuit breaker implementation for resilience patterns
 */
@Slf4j
@Component
public class CircuitBreaker {

  private final CircuitBreakerConfig config;
  private final AtomicReference<CircuitBreakerState> state = new AtomicReference<>(CircuitBreakerState.CLOSED);
  private final AtomicInteger failureCount = new AtomicInteger(0);
  private final AtomicInteger successCount = new AtomicInteger(0);
  private final AtomicLong lastFailureTime = new AtomicLong(0);
  private final AtomicLong totalRequests = new AtomicLong(0);
  private final AtomicLong totalFailures = new AtomicLong(0);
  private final AtomicLong totalSuccesses = new AtomicLong(0);

  public CircuitBreaker(CircuitBreakerConfig config) {
    this.config = config;
    log.info("Circuit breaker '{}' initialized with config: {}", config.getName(), config);
  }

  /**
   * Executes a supplier with circuit breaker protection
   */
  public <T> T execute(Supplier<T> supplier) {
    totalRequests.incrementAndGet();

    if (state.get() == CircuitBreakerState.OPEN) {
      if (shouldAttemptReset()) {
        state.set(CircuitBreakerState.HALF_OPEN);
        log.info("Circuit breaker '{}' transitioning to HALF_OPEN", config.getName());
      } else {
        totalFailures.incrementAndGet();
        throw new CircuitBreakerOpenException(
                "Circuit breaker '" + config.getName() + "' is OPEN");
      }
    }

    try {
      T result = supplier.get();
      onSuccess();
      return result;

    } catch (Exception e) {
      onFailure();
      throw e;
    }
  }

  private void onSuccess() {
    totalSuccesses.incrementAndGet();

    if (state.get() == CircuitBreakerState.HALF_OPEN) {
      int currentSuccessCount = successCount.incrementAndGet();
      if (currentSuccessCount >= config.getSuccessThreshold()) {
        reset();
      }
    } else {
      failureCount.set(0);
    }
  }

  private void onFailure() {
    totalFailures.incrementAndGet();
    lastFailureTime.set(Instant.now().toEpochMilli());

    int currentFailureCount = failureCount.incrementAndGet();

    if (state.get() == CircuitBreakerState.HALF_OPEN) {
      // If we're in half-open and get a failure, go back to open
      state.set(CircuitBreakerState.OPEN);
      successCount.set(0);
      log.warn("Circuit breaker '{}' returning to OPEN state after failure in HALF_OPEN",
              config.getName());
    } else if (currentFailureCount >= config.getFailureThreshold()) {
      state.set(CircuitBreakerState.OPEN);
      log.error("Circuit breaker '{}' OPENED after {} failures",
              config.getName(), currentFailureCount);
    }
  }

  private boolean shouldAttemptReset() {
    long timeSinceLastFailure = Instant.now().toEpochMilli() - lastFailureTime.get();
    return timeSinceLastFailure >= config.getTimeout().toMillis();
  }

  private void reset() {
    failureCount.set(0);
    successCount.set(0);
    state.set(CircuitBreakerState.CLOSED);
    log.info("Circuit breaker '{}' CLOSED - service recovered", config.getName());
  }

  /**
   * Gets current circuit breaker statistics
   */
  public CircuitBreakerStats getStats() {
    return CircuitBreakerStats.builder()
            .name(config.getName())
            .state(state.get())
            .failureCount(failureCount.get())
            .successCount(successCount.get())
            .totalRequests(totalRequests.get())
            .totalFailures(totalFailures.get())
            .totalSuccesses(totalSuccesses.get())
            .failureRate(calculateFailureRate())
            .lastFailureTime(lastFailureTime.get())
            .build();
  }

  private double calculateFailureRate() {
    long total = totalRequests.get();
    if (total == 0) {
      return 0.0;
    }
    return (double) totalFailures.get() / total;
  }

  /**
   * Gets current state of the circuit breaker
   */
  public CircuitBreakerState getState() {
    return state.get();
  }

  /**
   * Statistics for circuit breaker
   */
  @lombok.Data
  @lombok.Builder
  public static class CircuitBreakerStats {
    private String name;
    private CircuitBreakerState state;
    private int failureCount;
    private int successCount;
    private long totalRequests;
    private long totalFailures;
    private long totalSuccesses;
    private double failureRate;
    private long lastFailureTime;
  }
}

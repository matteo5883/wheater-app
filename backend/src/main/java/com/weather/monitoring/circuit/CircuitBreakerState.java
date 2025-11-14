package com.weather.monitoring.circuit;

/**
 * Circuit breaker states
 */
public enum CircuitBreakerState {

  /**
   * Circuit is closed - requests are allowed through
   */
  CLOSED,

  /**
   * Circuit is open - requests are blocked and fail fast
   */
  OPEN,

  /**
   * Circuit is half-open - limited requests are allowed to test if service recovered
   */
  HALF_OPEN
}

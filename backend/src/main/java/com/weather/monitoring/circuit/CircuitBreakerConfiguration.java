package com.weather.monitoring.circuit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for circuit breaker
 */
@Configuration
public class CircuitBreakerConfiguration {

  /**
   * Creates a CircuitBreakerConfig bean with default values
   */
  @Bean
  public CircuitBreakerConfig circuitBreakerConfig() {
    return CircuitBreakerConfig.builder()
            .name("weather-service-circuit-breaker")
            .build();
  }
}

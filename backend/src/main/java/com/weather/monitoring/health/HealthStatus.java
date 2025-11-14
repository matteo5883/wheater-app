package com.weather.monitoring.health;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Health check result with detailed status information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatus {

    private boolean healthy;
    private String status; // UP, DOWN, DEGRADED
    private String service;
    private LocalDateTime timestamp;
    private long responseTimeMs;
    private String message;
    private Map<String, Object> details;

    public static HealthStatus up(String service) {
        return HealthStatus.builder()
                .healthy(true)
                .status("UP")
                .service(service)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static HealthStatus down(String service, String message) {
        return HealthStatus.builder()
                .healthy(false)
                .status("DOWN")
                .service(service)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static HealthStatus degraded(String service, String message) {
        return HealthStatus.builder()
                .healthy(true)
                .status("DEGRADED")
                .service(service)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

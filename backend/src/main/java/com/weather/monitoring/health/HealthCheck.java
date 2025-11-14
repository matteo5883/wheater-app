package com.weather.monitoring.health;

/**
 * Interface for health check implementations
 */
public interface HealthCheck {

    /**
     * Performs a health check
     * @return health status result
     */
    HealthStatus check();

    /**
     * Returns the name of this health check
     * @return health check name
     */
    String getName();

    /**
     * Returns the timeout for this health check in milliseconds
     * @return timeout in ms
     */
    default long getTimeoutMs() {
        return 5000; // 5 seconds default
    }
}

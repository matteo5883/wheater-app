package com.weather.monitoring.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Health check for cache system
 */
@Slf4j
@RequiredArgsConstructor
public class CacheHealthCheck implements HealthCheck {

  private final CacheManager cacheManager;

  @Override
  public HealthStatus check() {
    long startTime = System.currentTimeMillis();

    try {
      // Test cache operations - check if caches are accessible
      long cacheCount = cacheManager.getCacheNames().size();
      boolean cachesAccessible = true;

      // Try to access each cache to verify they're working
      for (String cacheName : cacheManager.getCacheNames()) {
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
          cachesAccessible = false;
          break;
        }
      }

      long responseTime = System.currentTimeMillis() - startTime;

      Map<String, Object> details = new HashMap<>();
      details.put("cache_count", cacheCount);
      details.put("caches_accessible", cachesAccessible);
      details.put("cache_names", cacheManager.getCacheNames());
      details.put("response_time_ms", responseTime);

      HealthStatus status = HealthStatus.up("cache");
      status.setResponseTimeMs(responseTime);
      status.setDetails(details);

      // Check cache accessibility
      if (!cachesAccessible) {
        return HealthStatus.down("cache", "One or more caches are not accessible");
      }

      return status;

    } catch (Exception e) {
      long responseTime = System.currentTimeMillis() - startTime;

      Map<String, Object> details = new HashMap<>();
      details.put("error", e.getMessage());
      details.put("response_time_ms", responseTime);

      HealthStatus status = HealthStatus.down("cache", e.getMessage());
      status.setResponseTimeMs(responseTime);
      status.setDetails(details);

      log.error("Cache health check failed", e);
      return status;
    }
  }

  @Override
  public String getName() {
    return "cache";
  }
}

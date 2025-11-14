package com.weather.api.controller;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for monitoring Spring Cache statistics and management
 */
@RestController
@RequestMapping("/api/v1/cache")
@Tag(name = "Cache Management", description = "Spring Cache monitoring and management")
@RequiredArgsConstructor
public class SpringCacheController {

  private final CacheManager cacheManager;

  /**
   * Get statistics for all caches
   */
  @GetMapping("/stats")
  @Operation(summary = "Get cache statistics", description = "Retrieve statistics for all configured caches")
  public Map<String, Object> getCacheStatistics() {
    Map<String, Object> stats = new HashMap<>();

    Collection<String> cacheNames = cacheManager.getCacheNames();
    stats.put("totalCaches", cacheNames.size());
    stats.put("cacheNames", cacheNames);

    Map<String, CacheStats> cacheStats = new HashMap<>();
    for (String cacheName : cacheNames) {
      Cache cache = cacheManager.getCache(cacheName);
      if (cache instanceof CaffeineCache) {
        CaffeineCache caffeineCache = (CaffeineCache) cache;
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                caffeineCache.getNativeCache();
        cacheStats.put(cacheName, nativeCache.stats());
      }
    }
    stats.put("statistics", cacheStats);

    return stats;
  }

  /**
   * Get statistics for a specific cache
   */
  @GetMapping("/stats/{cacheName}")
  @Operation(summary = "Get specific cache statistics")
  public Map<String, Object> getCacheStatistics(@PathVariable String cacheName) {
    Cache cache = cacheManager.getCache(cacheName);
    if (cache == null) {
      throw new RuntimeException("Cache not found: " + cacheName);
    }

    Map<String, Object> result = new HashMap<>();
    result.put("cacheName", cacheName);

    if (cache instanceof CaffeineCache) {
      CaffeineCache caffeineCache = (CaffeineCache) cache;
      com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
              caffeineCache.getNativeCache();

      CacheStats stats = nativeCache.stats();
      Map<String, Object> statsMap = new HashMap<>();
      statsMap.put("hitCount", stats.hitCount());
      statsMap.put("missCount", stats.missCount());
      statsMap.put("hitRate", stats.hitRate());
      statsMap.put("missRate", stats.missRate());
      statsMap.put("requestCount", stats.requestCount());
      statsMap.put("evictionCount", stats.evictionCount());
      statsMap.put("estimatedSize", nativeCache.estimatedSize());

      result.put("statistics", statsMap);
    }

    return result;
  }

  /**
   * Get cache configuration
   */
  @GetMapping("/config")
  @Operation(summary = "Get cache configuration")
  public Map<String, Object> getCacheConfiguration() {
    Map<String, Object> config = new HashMap<>();
    config.put("cacheManagerType", cacheManager.getClass().getSimpleName());
    config.put("cacheNames", cacheManager.getCacheNames());

    Collection<String> cacheNames = cacheManager.getCacheNames();
    for (String cacheName : cacheNames) {
      Cache cache = cacheManager.getCache(cacheName);
      if (cache instanceof CaffeineCache) {
        config.put("provider", "Caffeine");
        break;
      }
    }

    return config;
  }
}

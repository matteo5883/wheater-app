package com.weather.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

/**
 * Spring Cache configuration using Caffeine as the cache provider
 */
@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "weather.cache")
@Getter
@Setter
public class CacheConfig {

  private Duration expiration;
  private int maximumSize;
  private boolean recordStats;

  @PostConstruct
  public void init() {
    // Set defaults if values are null/empty
    if (expiration == null) {
      expiration = Duration.ofMinutes(30);
    }
    if (maximumSize <= 0) {
      maximumSize = 1000;
    }
  }

  /**
   * Primary cache manager using Caffeine
   */
  @Bean
  @Primary
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();

    cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(maximumSize)
            .expireAfterWrite(expiration)
            .recordStats() // Enable statistics
    );

    // Pre-create cache names
    cacheManager.setCacheNames(java.util.Arrays.asList("weather-current", "weather-forecast", "weather-alerts"));

    return cacheManager;
  }

}

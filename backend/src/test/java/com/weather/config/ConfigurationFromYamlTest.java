package com.weather.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "weather.cache.expiration=PT45M",
        "weather.cache.maximum-size=2000",
        "weather.cache.record-stats=false",
        "weather.api-timeout-seconds=60",
        "weather.max-cache-size=5000"
})
class ConfigurationFromYamlTest {

  @Autowired
  private CacheConfig cacheConfig;

  @Autowired
  private WeatherApiProperties weatherApiProperties;

  @Test
  void shouldLoadCacheConfigurationFromYaml() {
    assertThat(cacheConfig.getExpiration()).isEqualTo(Duration.ofMinutes(45));
    assertThat(cacheConfig.getMaximumSize()).isEqualTo(2000);
    assertThat(cacheConfig.isRecordStats()).isFalse();
  }

  @Test
  void shouldLoadWeatherApiPropertiesFromYaml() {
    assertThat(weatherApiProperties.getApiTimeoutSeconds()).isEqualTo(60);
    assertThat(weatherApiProperties.getMaxCacheSize()).isEqualTo(5000);
  }

  @Test
  void shouldHaveNoHardcodedDefaults() {
    assertThat(cacheConfig.getExpiration().toMinutes())
            .isNotEqualTo(30)
            .isEqualTo(45);

    assertThat(cacheConfig.getMaximumSize())
            .isNotEqualTo(1000)
            .isEqualTo(2000);
  }
}

package com.weather.config;

import lombok.Data;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for HTTP client settings
 */
@Configuration
@ConfigurationProperties(prefix = "weather.http-client")
@Data
public class HttpClientConfig {

  private long connectTimeoutSeconds;
  private long readTimeoutSeconds;
  private long writeTimeoutSeconds;

  /**
   * Creates and configures OkHttpClient bean
   */
  @Bean
  public OkHttpClient okHttpClient() {
    return new OkHttpClient.Builder()
            .connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(readTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(writeTimeoutSeconds, TimeUnit.SECONDS)
            .build();
  }
}

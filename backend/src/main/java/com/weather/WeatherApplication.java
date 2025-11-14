package com.weather;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Spring Boot main application class for WeatherApp
 */
@SpringBootApplication
@EnableConfigurationProperties
@EnableCaching
@EnableAspectJAutoProxy
@Slf4j
public class WeatherApplication {

  public static void main(String[] args) {
    log.info("🌤️ Starting WeatherApp with Spring Boot...");
    SpringApplication.run(WeatherApplication.class, args);
    log.info("🚀 WeatherApp started successfully!");
  }
}

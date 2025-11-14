package com.weather.service;

/**
 * Exception thrown when weather service operations fail
 */
public class WeatherServiceException extends RuntimeException {

  public WeatherServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
